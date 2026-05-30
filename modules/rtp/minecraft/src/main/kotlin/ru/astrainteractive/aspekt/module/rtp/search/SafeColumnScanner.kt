package ru.astrainteractive.aspekt.module.rtp.search

import kotlinx.coroutines.yield
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.chunk.LevelChunk
import ru.astrainteractive.astralibs.server.location.KLocation

internal class SafeColumnScanner {

    @Suppress("LoopWithTooManyJumpStatements", "ReturnCount")
    suspend fun findStandableLocation(
        chunk: LevelChunk,
        worldName: String,
        minY: Int,
        maxY: Int,
    ): KLocation? {
        val pos = chunk.pos
        for (x in pos.minBlockX until pos.maxBlockX) {
            for (z in pos.minBlockZ until pos.maxBlockZ) {
                for (y in minY until maxY) {
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
                        x = x.toDouble() + BLOCK_CENTER_OFFSET,
                        y = y.toDouble(),
                        z = z.toDouble() + BLOCK_CENTER_OFFSET,
                        worldName = worldName
                    )
                }
            }
        }
        return null
    }

    private companion object {
        private const val BLOCK_CENTER_OFFSET = 0.5
    }
}
