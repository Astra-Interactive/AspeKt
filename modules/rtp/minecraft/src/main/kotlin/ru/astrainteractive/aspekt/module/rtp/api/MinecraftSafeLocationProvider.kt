package ru.astrainteractive.aspekt.module.rtp.api

import com.google.common.cache.CacheBuilder
import java.util.Comparator
import java.util.UUID
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
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
import net.minecraft.server.level.TicketType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.chunk.ChunkStatus
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.storage.ServerLevelData
import net.minecraft.world.phys.AABB
import ru.astrainteractive.astralibs.server.location.KLocation
import ru.astrainteractive.astralibs.server.util.MinecraftUtil
import ru.astrainteractive.astralibs.server.util.getNextTickTime
import ru.astrainteractive.astralibs.server.util.getOnlinePlayer
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.core.util.cast
import ru.astrainteractive.klibs.mikro.core.util.tryCast

class MinecraftSafeLocationProvider(
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
                    // Center within the block so the player's bounding box does not straddle
                    // into a neighboring (possibly unloaded) chunk on the first tick.
                    return KLocation(
                        x = x.toDouble() + 0.5,
                        y = y.toDouble(),
                        z = z.toDouble() + 0.5,
                        worldName = worldName
                    )
                }
            }
        }
        return null
    }

    private fun LevelChunk.isHazardousArea(): Boolean {
        val area = AABB(
            this.pos.minBlockX.toDouble(),
            level.minBuildHeight.toDouble(),
            this.pos.minBlockZ.toDouble(),
            (this.pos.maxBlockX + 1).toDouble(),
            level.maxBuildHeight.toDouble(),
            (this.pos.maxBlockZ + 1).toDouble()
        )
        return level.getEntities(
            null,
            area,
            { entity ->
                val key = EntityType.getKey(entity.type)

                key.namespace in HAZARD_ENTITY_NAMESPACES
            }
        ).isNotEmpty()
    }

    /**
     * Loads (generating if needed) the chunk to [net.minecraft.world.level.chunk.ChunkStatus.FULL] without blocking the server
     * thread. Two rules make this work:
     *  - [net.minecraft.server.level.ServerChunkCache.getChunkFuture] is called OFF the main
     *    thread (via [KotlinDispatchers.IO]), so it returns a real async future instead of
     *    blocking via managedBlock.
     *  - We register our own [RTP_TICKET] first. The ticket the engine adds internally
     *    (TicketType.UNKNOWN) expires after a single tick, so a freshly generated far chunk
     *    resolves to UNLOADED before generation finishes. Our ticket keeps it alive long enough
     *    to reach FULL, and auto-expires afterwards (no manual cleanup).
     */
    private suspend fun loadChunkToFull(level: ServerLevel, chunkX: Int, chunkZ: Int): LevelChunk? {
        val chunkPos = ChunkPos(chunkX, chunkZ)
        withContext(kotlinDispatchers.Main) {
            level.chunkSource.addRegionTicket(RTP_TICKET, chunkPos, 0, chunkPos)
        }
        return level.chunkSource.getChunkFuture(chunkX, chunkZ, ChunkStatus.FULL, true)
            .await()
            .ifRight { failure ->
                error { "#loadChunkToFull fail: $failure" }
            }
            .left()
            .orElse(null)
            ?.tryCast<LevelChunk>()
    }

    /**
     * Generates and keeps loaded a (2 * [DESTINATION_RADIUS] + 1)^2 chunk area around the
     * destination BEFORE the player is teleported there.
     *
     * Each chunk is loaded via [loadChunkToFull], i.e. with a radius-0 ticket -> the chunks reach
     * [ChunkStatus.FULL] (block reads succeed) but are NOT block-ticking, so block entities such
     * as brewing stands do not tick during the bridge window. That matters because a ticking
     * block entity at the edge of the loaded area can trigger a neighbour-shape update that reads
     * a still-ungenerated chunk and force-generates it synchronously on the server thread, which
     * deadlocks the chunk system. A generous FULL radius also covers the player's own first-tick
     * neighbour reads until its [net.minecraft.server.level.TicketType.PLAYER] ticket takes over.
     */
    private suspend fun prepareDestination(level: ServerLevel, chunkX: Int, chunkZ: Int): List<LevelChunk> {
        val simulationDistance = MinecraftUtil.serverOrNull?.playerList?.simulationDistance ?: 1
        return (-simulationDistance..simulationDistance).flatMap { dx ->
            (-simulationDistance..simulationDistance).map { dz ->
                val chunk = loadChunkToFull(level, chunkX + dx, chunkZ + dz)
                yield()
                chunk
            }
        }.filterNotNull()
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
            val chunk = loadChunkToFull(level, chunkX, chunkZ)
            info { "#findSafeLocation chunk find finished: $chunk" }
            if (chunk == null) {
                retryCount++
                error { "#findSafeLocation could not load chunk. Retries: $retryCount" }
                yield()
                continue
            }
            if (chunk.isHazardousArea()) {
                retryCount++
                error { "#findSafeLocation chunk is hazardous. Retries: $retryCount" }
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
            if (location != null) {
                // Pre-generate & hold the destination area loaded before the teleport happens.
                val isHazardous = prepareDestination(level, chunkX, chunkZ).any { chunk -> chunk.isHazardousArea() }
                if (isHazardous) {
                    yield()
                    continue
                }
                return location
            }

            retryCount++
            error { "#findSafeLocation could not find safe location. Retries: $retryCount" }
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
        return jobMap[uuid]?.isActive == true
    }

    override fun hasTimeout(uuid: UUID): Boolean {
        val hasTimeout = timeout.getIfPresent(uuid) != null
        if (!hasTimeout) timeout.put(uuid, Unit)
        return hasTimeout
    }

    private companion object {
        // ~10s: long enough for a far chunk to finish generating and to bridge the teleport until
        // the player's own ticket takes over, short enough that rejected scan candidates unload.
        private const val RTP_TICKET_TIMEOUT_TICKS = 200

        private val RTP_TICKET: TicketType<ChunkPos> = TicketType.create(
            "aspekt_rtp",
            Comparator.comparingLong { pos: ChunkPos -> pos.toLong() },
            RTP_TICKET_TIMEOUT_TICKS
        )

        private val HAZARD_ENTITY_NAMESPACES = setOf(
            "mowziesmobs",
            "cataclysm",
            "bosses_of_mass_destruction",
            "conjurer_illager",
            "brutalbosses",
            "create"
        )
    }
}