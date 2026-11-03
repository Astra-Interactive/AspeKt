package ru.astrainteractive.aspekt.module.rtp.minecraft.search

import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.phys.AABB

internal class ChunkHazardDetector {

    fun isHazardous(chunk: LevelChunk, hazardNamespaces: Collection<String>): Boolean {
        val level = chunk.level
        val area = AABB(
            chunk.pos.minBlockX.toDouble(),
            level.minBuildHeight.toDouble(),
            chunk.pos.minBlockZ.toDouble(),
            chunk.pos.maxBlockX.plus(1).toDouble(),
            level.maxBuildHeight.toDouble(),
            chunk.pos.maxBlockZ.plus(1).toDouble()
        )
        return level.getEntities(
            null,
            area
        ) { entity ->
            EntityType.getKey(entity.type).namespace in hazardNamespaces
        }.isNotEmpty()
    }
}
