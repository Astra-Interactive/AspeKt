package ru.astrainteractive.aspekt.module.claims.api.usecase

import ru.astrainteractive.aspekt.module.claims.api.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.api.model.UniqueWorldKey
import ru.astrainteractive.aspekt.module.claims.api.util.uniqueWorldKey

class CheckChunkActionUseCase(
    private val claimsRepository: ClaimsRepository
) {
    operator fun invoke(
        key: UniqueWorldKey,
        chunkFlag: ChunkFlag,
        claimPlayer: ClaimPlayer? = null
    ): Boolean {
        val chunkValue = claimsRepository.loadAllChunks()
            .firstOrNull { claimChunk -> claimChunk.uniqueWorldKey == key }
            ?.flags
            ?.get(chunkFlag)
            ?: true
        val krate = claimsRepository.chunkByKrate[key]
        if (krate == null) return chunkValue
        if (krate.cachedStateFlow.value.ownerUUID == claimPlayer?.uuid) return true
        if (krate.cachedStateFlow.value.members.map(ClaimPlayer::uuid).contains(claimPlayer?.uuid)) return true
        return chunkValue
    }
}
