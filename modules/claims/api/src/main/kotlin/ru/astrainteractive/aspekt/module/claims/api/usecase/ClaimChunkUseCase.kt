package ru.astrainteractive.aspekt.module.claims.api.usecase

import ru.astrainteractive.aspekt.module.claims.api.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.api.model.UnderClaimException
import ru.astrainteractive.aspekt.module.claims.api.util.uniqueWorldKey
import java.util.UUID

internal class ClaimChunkUseCase(
    private val claimsRepository: ClaimsRepository
) {
    suspend operator fun invoke(uuid: UUID, claimChunk: ClaimChunk): Result<Unit> {
        claimsRepository.claimOwnerUuid(claimChunk.uniqueWorldKey)?.let { ownerUuid ->
            return Result.failure(UnderClaimException(ownerUuid))
        }
        return claimsRepository.saveChunk(
            uuid = uuid,
            chunk = claimChunk.copy(
                flags = ChunkFlag.entries.associateWith { false }
            )
        )
    }
}
