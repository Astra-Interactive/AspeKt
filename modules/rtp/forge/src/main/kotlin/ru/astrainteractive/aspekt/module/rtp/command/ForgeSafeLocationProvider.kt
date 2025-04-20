package ru.astrainteractive.aspekt.module.rtp.command

import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.util.cast
import java.util.UUID
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class ForgeSafeLocationProvider : SafeLocationProvider {
    private val mutex = Mutex()
    private val jobMap = HashMap<UUID, Deferred<Location>>()

    private val timeout = CacheBuilder
        .newBuilder()
        .expireAfterWrite(10.seconds.toJavaDuration())
        .build<UUID, Unit>()

    @Suppress("LoopWithTooManyJumpStatements")
    private fun safeLocationFlow(level: ServerLevel) = channelFlow {
        do {
            val x = Random.nextInt(-1000000, 1000000).toDouble()
            val z = Random.nextInt(-1000000, 1000000).toDouble()
            val worldName = level.levelData.cast<ServerLevelData>().levelName

            val blockPos = BlockPos.MutableBlockPos(x.toInt(), 50, z.toInt())
            val chunk = level.getChunkAt(blockPos)
            for (x in chunk.pos.minBlockX until chunk.pos.maxBlockX) {
                for (z in chunk.pos.minBlockZ until chunk.pos.maxBlockZ) {
                    for (y in 30 until 100) {
                        val blockPos = BlockPos(x, y, z)
                        val block = chunk.getBlockState(blockPos).block
                        if (block is LiquidBlock) continue
                        if (block !is AirBlock) continue
                        val location = Location(
                            x = x.toDouble(),
                            y = y.toDouble(),
                            z = z.toDouble(),
                            worldName = worldName
                        )
                        send(location)
                    }
                }
            }
        } while (currentCoroutineContext().isActive)
    }

    override fun getJobsNumber(): Int {
        return jobMap.size
    }

    override fun isActive(uuid: UUID): Boolean {
        return uuid in jobMap
    }

    override fun hasTimeout(uuid: UUID): Boolean {
        val hasTimeout = timeout.getIfPresent(uuid) != null
        if (!hasTimeout) timeout.put(uuid, Unit)
        return hasTimeout
    }

    override suspend fun getLocation(scope: CoroutineScope, uuid: UUID): Location? {
        return mutex.withLock {
            val player = ForgeUtil.getOnlinePlayer(uuid) ?: return@withLock null
            val deferred = jobMap.getOrPut(uuid) {
                scope.async { safeLocationFlow(player.level().cast<ServerLevel>()).first() }
            }
            deferred.invokeOnCompletion {
                jobMap.remove(uuid)
            }
            deferred.await()
        }
    }
}
