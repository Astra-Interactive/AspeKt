package ru.astrainteractive.aspekt.module.claims.api.usecase

import ru.astrainteractive.aspekt.module.claims.api.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk

internal class CreateClaimGridUseCase(
    private val claimsRepository: ClaimsRepository
) {
    operator fun invoke(size: Int, chunk: ClaimChunk): List<List<Boolean>> {
        val chunks = claimsRepository.loadAllChunks()
        return List(size) { i ->
            List(size) { j ->
                val x = chunk.x - size / 2 + i
                val z = chunk.z + size / 2 - j
                chunks.any { claimChunk -> claimChunk.x == x && claimChunk.z == z }
            }
        }
    }
}
