package ru.astrainteractive.aspekt.module.rtp.command

import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.future.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.chunk.ChunkStatus
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.astralibs.server.location.KLocation
import ru.astrainteractive.astralibs.server.util.MinecraftUtil
import ru.astrainteractive.astralibs.server.util.getNextTickTime
import ru.astrainteractive.astralibs.server.util.getOnlinePlayer
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.core.util.cast
import ru.astrainteractive.klibs.mikro.core.util.tryCast
import java.util.UUID
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class ForgeSafeLocationProvider(
    private val kotlinDispatchers: KotlinDispatchers
) : SafeLocationProvider, Logger by JUtiltLogger("SafeLocationProvider") {
    private val mutex = Mutex()
    private val jobMap = HashMap<UUID, Deferred<KLocation?>>()

    private val timeout = CacheBuilder
        .newBuilder()
        .expireAfterWrite(10.seconds.toJavaDuration())
        .build<UUID, Unit>()

    @Suppress("LoopWithTooManyJumpStatements", "MagicNumber", "ReturnCount")
    private suspend fun findSafeLocation(chunk: LevelChunk, worldName: String): KLocation? {
        val pos: ChunkPos = chunk.pos
        for (x in pos.minBlockX until pos.maxBlockX) {
            for (z in pos.minBlockZ until pos.maxBlockZ) {
                for (y in 30 until 100) {
                    val block = chunk.getBlockState(BlockPos(x, y, z)).block
                    if (block is LiquidBlock) {
                        yield()
                        continue
                    }
                    if (block !is AirBlock) {
                        yield()
                        continue
                    }
                    return KLocation(
                        x = x.toDouble(),
                        y = y.toDouble(),
                        z = z.toDouble(),
                        worldName = worldName
                    )
                }
            }
        }
        return null
    }

    @Suppress("LoopWithTooManyJumpStatements", "MagicNumber")
    private suspend fun findSafeLocation(level: ServerLevel): KLocation? {
        var retryCount = 0
        val worldName = level.levelData.cast<ServerLevelData>().levelName
        do {
            val blockX = Random.nextInt(-100000, 100000)
            val blockZ = Random.nextInt(-100000, 100000)
            val chunkX = SectionPos.blockToSectionCoord(blockX)
            val chunkZ = SectionPos.blockToSectionCoord(blockZ)
            info { "#findSafeLocation started finding chunk..." }
            val chunk = withContext(kotlinDispatchers.Main) {
                level.chunkSource.getChunkFuture(
                    chunkX,
                    chunkZ,
                    ChunkStatus.FULL,
                    true
                )
            }.await().left().orElse(null)?.tryCast<LevelChunk>()
            info { "#findSafeLocation chunk find finished: $chunk" }
            if (chunk == null) {
                retryCount++
                error { "#safeLocationFlow could not find chunk. Retries: $retryCount" }
                yield()
                continue
            }
            yield()

            // Scan the already-loaded chunk in a single main-thread hop (block reads must
            // stay on the server thread), returning the first standable column found.
            info { "#findSafeLocation started finding safe location..." }
            val location = withContext(kotlinDispatchers.Main) {
                findSafeLocation(chunk, worldName)
            }
            info { "#findSafeLocation safe location found: $location" }
            if (location != null) return location

            retryCount++
            error { "#safeLocationFlow could not find safe location. Retries: $retryCount" }
            yield()
        } while (currentCoroutineContext().isActive)
        return null
    }

    override suspend fun getLocation(ioScope: CoroutineScope, uuid: UUID): KLocation? {
        return mutex.withLock {
            val player = MinecraftUtil.getOnlinePlayer(uuid) ?: return@withLock null
            val deferred = jobMap.getOrPut(uuid) {
                ioScope.async { findSafeLocation(player.level().cast<ServerLevel>()) }
            }
            deferred.invokeOnCompletion {
                jobMap.remove(uuid)
            }
            deferred.await()
        }
    }
    
    override fun getJobsNumber(): Int {
        return jobMap.size
    }

    override fun getNextTickTime(): Double = MinecraftUtil.getNextTickTime()

    override fun isActive(uuid: UUID): Boolean {
        return uuid in jobMap
    }

    override fun hasTimeout(uuid: UUID): Boolean {
        val hasTimeout = timeout.getIfPresent(uuid) != null
        if (!hasTimeout) timeout.put(uuid, Unit)
        return hasTimeout
    }
}
