package ru.astrainteractive.aspekt.module.rtp.search

import kotlinx.coroutines.yield
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import ru.astrainteractive.astralibs.server.util.MinecraftUtil

internal class DestinationPreparer(private val chunkLoader: ChunkLoader) {
    suspend fun prepare(level: ServerLevel, chunkX: Int, chunkZ: Int): List<LevelChunk> {
        val simulationDistance = MinecraftUtil.serverOrNull
            ?.playerList
            ?.simulationDistance
            ?: 1
        return (-simulationDistance..simulationDistance).flatMap { dx ->
            (-simulationDistance..simulationDistance).map { dz ->
                val chunk = chunkLoader.loadToFull(level, chunkX + dx, chunkZ + dz)
                yield()
                chunk
            }
        }.filterNotNull()
    }
}
