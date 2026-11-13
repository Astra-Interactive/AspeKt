package ru.astrainteractive.aspekt.module.claims.api.usecase

import ru.astrainteractive.aspekt.module.claims.api.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.api.model.UniqueWorldKey
import ru.astrainteractive.aspekt.module.claims.api.util.isChunkActionAllowed
import ru.astrainteractive.aspekt.module.claims.api.util.uniqueWorldKey

class CheckChunkActionUseCase(
    private val claimsRepository: ClaimsRepository
) {
    operator fun invoke(
        key: UniqueWorldKey,
        chunkFlag: ChunkFlag,
        claimPlayer: ClaimPlayer? = null
    ): Boolean {
        val claimChunk = claimsRepository.loadAllChunks()
            .firstOrNull { loadedChunk -> loadedChunk.uniqueWorldKey == key }
        return isChunkActionAllowed(
            claimChunk = claimChunk,
            chunkFlag = chunkFlag,
            claimData = claimsRepository.findClaimData(key),
            claimPlayer = claimPlayer
        )
    }
}
