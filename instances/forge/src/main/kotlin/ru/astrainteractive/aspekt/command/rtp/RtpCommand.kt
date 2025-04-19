package ru.astrainteractive.aspekt.command.rtp

import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.storage.ServerLevelData
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.literal
import ru.astrainteractive.aspekt.core.forge.coroutine.ForgeMainDispatcher
import ru.astrainteractive.aspekt.core.forge.minecraft.teleport.ForgeTeleportApi
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.messenger.MinecraftMessenger
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.util.cast
import ru.astrainteractive.aspekt.util.tryCast
import ru.astrainteractive.astralibs.string.StringDesc
import java.util.UUID
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object SafeLocationProvider {
    private val mutex = Mutex()
    private val jobMap = HashMap<UUID, Deferred<Location>>()

    private val timeout = CacheBuilder
        .newBuilder()
        .expireAfterWrite(10.seconds.toJavaDuration())
        .build<UUID, Unit>()

    private fun safeLocationFlow(level: ServerLevel) = channelFlow {
        do {
            val x = Random.nextInt(-10000, 10000).toDouble()
            val z = Random.nextInt(-10000, 10000).toDouble()
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

    fun getJobsNumber(): Int {
        return jobMap.size
    }

    fun isActive(player: ServerPlayer): Boolean {
        return player.uuid in jobMap
    }

    fun hasTimeout(player: ServerPlayer): Boolean {
        val hasTimeout = timeout.getIfPresent(player.uuid) != null
        if (!hasTimeout) timeout.put(player.uuid, Unit)
        return hasTimeout
    }

    suspend fun getLocation(scope: CoroutineScope, player: ServerPlayer): Deferred<Location> {
        return mutex.withLock {
            val deferred = jobMap.getOrPut(player.uuid) {
                scope.async { safeLocationFlow(player.level().cast<ServerLevel>()).first() }
            }
            deferred.invokeOnCompletion {
                jobMap.remove(player.uuid)
            }
            deferred
        }
    }
}

fun RegisterCommandsEvent.rtp(
    scope: CoroutineScope,
    messenger: MinecraftMessenger
) {
    literal(
        alias = "rtp",
        execute = { ctx ->
            val player = ctx.source.player?.tryCast<ServerPlayer>() ?: return@literal
            if (SafeLocationProvider.getJobsNumber() > 0) {
                messenger.send(player.uuid, StringDesc.Raw("Max RTP jobs are reached!"))
                return@literal
            }
            if (SafeLocationProvider.isActive(player)) return@literal
            if (SafeLocationProvider.hasTimeout(player)) {
                messenger.send(player.uuid, StringDesc.Raw("Timeout..."))
                return@literal
            }
            scope.launch {
                val location = SafeLocationProvider.getLocation(this, player).await()
                messenger.send(player.uuid, StringDesc.Raw("Found place for you!"))
                withContext(ForgeMainDispatcher) {
                    ForgeTeleportApi().teleport(OnlineMinecraftPlayer(player.uuid, player.name.toPlain()), location)
                }
            }
        }
    ).run(dispatcher::register)
}
