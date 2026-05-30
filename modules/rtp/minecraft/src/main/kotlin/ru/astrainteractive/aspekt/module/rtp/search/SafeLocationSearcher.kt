package ru.astrainteractive.aspekt.module.rtp.search

import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import net.minecraft.core.SectionPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.module.rtp.api.RtpSearchResult
import ru.astrainteractive.aspekt.module.rtp.model.RtpConfig
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.core.util.cast
import kotlin.random.Random

internal class SafeLocationSearcher(
    private val dispatchers: KotlinDispatchers,
    private val chunkLoader: ChunkLoader = ChunkLoader(dispatchers),
    private val hazardDetector: ChunkHazardDetector = ChunkHazardDetector(),
    private val columnScanner: SafeColumnScanner = SafeColumnScanner(),
    private val destinationPreparer: DestinationPreparer = DestinationPreparer(chunkLoader),
) : Logger by JUtiltLogger("SafeLocationProvider") {

    @Suppress("LoopWithTooManyJumpStatements", "ReturnCount")
    suspend fun findSafeLocation(level: ServerLevel, config: RtpConfig): RtpSearchResult {
        val worldName = level.levelData.cast<ServerLevelData>().levelName
        repeat(config.maxRetryCount) { attempt ->
            val chunkX = SectionPos.blockToSectionCoord(Random.nextInt(config.minX, config.maxX))
            val chunkZ = SectionPos.blockToSectionCoord(Random.nextInt(config.minZ, config.maxZ))
            info { "#findSafeLocation started finding chunk..." }
            val chunk = chunkLoader.loadToFull(level, chunkX, chunkZ)
            info { "#findSafeLocation chunk find finished: $chunk" }
            if (chunk == null) {
                error { "#findSafeLocation could not load chunk. Attempt: ${attempt + 1}" }
                yield()
                return@repeat
            }
            if (hazardDetector.isHazardous(chunk, config.hazardNamespaces)) {
                error { "#findSafeLocation chunk is hazardous. Attempt: ${attempt + 1}" }
                yield()
                return@repeat
            }
            yield()

            info { "#findSafeLocation started finding safe location..." }
            val location = withContext(dispatchers.Main) {
                columnScanner.findStandableLocation(
                    chunk = chunk,
                    worldName = worldName,
                    minY = config.minY,
                    maxY = config.maxY,
                )
            }
            info { "#findSafeLocation safe location found: $location" }
            if (location != null) {
                // Pre-generate & hold the destination area loaded before the teleport happens.
                val isHazardous = destinationPreparer.prepare(level, chunkX, chunkZ)
                    .any { preparedChunk -> hazardDetector.isHazardous(preparedChunk, config.hazardNamespaces) }
                if (isHazardous) {
                    yield()
                    return@repeat
                }
                return RtpSearchResult.Success(location)
            }

            error { "#findSafeLocation could not find safe location. Attempt: ${attempt + 1}" }
            yield()
        }
        error { "#findSafeLocation reached max retry count: ${config.maxRetryCount}" }
        return RtpSearchResult.MaxRetriesReached
    }
}
