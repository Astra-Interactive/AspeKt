package ru.astrainteractive.aspekt.module.autocrop.util

import org.bukkit.block.Block
import org.bukkit.block.BlockFace

private fun Block.collectRelativeBlocks(radius: Int, blocks: MutableSet<Block>) {
    if (radius <= 1) return
    if (!blocks.add(this)) return
    BlockFace.entries.forEach { blockFace -> getRelative(blockFace).collectRelativeBlocks(radius - 1, blocks) }
}

internal fun Block.relativeBlocks(radius: Int): Set<Block> {
    val blocks = HashSet<Block>()
    collectRelativeBlocks(radius, blocks)
    return blocks
}
