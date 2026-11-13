@file:Suppress("Filename")

package ru.astrainteractive.aspekt.module.claims.api.util

import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.api.model.UniqueWorldKey

inline val ClaimChunk.uniqueWorldKey: UniqueWorldKey
    get() = UniqueWorldKey("${this.chunkKey}_${this.worldName}")

internal fun ClaimChunk.resetFlags(): ClaimChunk {
    return copy(flags = ChunkFlag.entries.associateWith { false })
}

internal fun ClaimChunk.updateFlag(flag: ChunkFlag, value: Boolean): ClaimChunk {
    return copy(
        flags = flags.toMutableMap().apply {
            this[flag] = value
        }
    )
}

internal fun ClaimChunk.buildClaimGrid(size: Int, claimedChunks: List<ClaimChunk>): List<List<Boolean>> {
    return List(size) { i ->
        List(size) { j ->
            val gridX = this.x - size / 2 + i
            val gridZ = this.z + size / 2 - j
            claimedChunks.any { claimChunk -> claimChunk.x == gridX && claimChunk.z == gridZ }
        }
    }
}
