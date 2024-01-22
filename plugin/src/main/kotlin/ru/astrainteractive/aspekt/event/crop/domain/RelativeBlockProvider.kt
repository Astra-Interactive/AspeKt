package ru.astrainteractive.aspekt.event.crop.domain

import org.bukkit.block.Block
import org.bukkit.block.BlockFace

/**
 * This class is required to (almost) fast get relative blocks
 */
class RelativeBlockProvider {
    private val map = HashSet<Block>()

    private fun fillWithRelativeBlocks(block: Block, radius: Int) {
        if (radius <= 1) return
        if (map.contains(block)) return
        map.add(block)
        BlockFace.values().forEach { blockFace -> fillWithRelativeBlocks(block.getRelative(blockFace), radius - 1) }
    }

    /**
     * Returns a set of block around [block] in radius of [radius]
     */
    fun provide(block: Block, radius: Int): Set<Block> {
        fillWithRelativeBlocks(block, radius)
        return map
    }
}
