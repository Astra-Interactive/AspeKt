package ru.astrainteractive.aspekt.module.claims.api.util

import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimData
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimPlayer

internal fun isChunkActionAllowed(
    claimChunk: ClaimChunk?,
    chunkFlag: ChunkFlag,
    claimData: ClaimData?,
    claimPlayer: ClaimPlayer?
): Boolean {
    val chunkValue = claimChunk
        ?.flags
        ?.get(chunkFlag)
        ?: true
    if (claimData == null) return chunkValue
    if (claimData.ownerUUID == claimPlayer?.uuid) return true
    if (claimData.members.map(ClaimPlayer::uuid).contains(claimPlayer?.uuid)) return true
    return chunkValue
}
