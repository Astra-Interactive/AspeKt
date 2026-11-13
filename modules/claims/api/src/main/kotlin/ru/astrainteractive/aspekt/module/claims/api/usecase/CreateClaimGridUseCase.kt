package ru.astrainteractive.aspekt.module.claims.api.usecase

import ru.astrainteractive.aspekt.module.claims.api.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.api.util.buildClaimGrid

internal class CreateClaimGridUseCase(
    private val claimsRepository: ClaimsRepository
) {
    operator fun invoke(size: Int, chunk: ClaimChunk): List<List<Boolean>> {
        val chunks = claimsRepository.loadAllChunks()
        return chunk.buildClaimGrid(size, chunks)
    }
}
