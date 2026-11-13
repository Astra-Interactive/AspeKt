package ru.astrainteractive.aspekt.module.claims.api.usecase

import ru.astrainteractive.aspekt.module.claims.api.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimNotFoundException
import ru.astrainteractive.aspekt.module.claims.api.model.UnderClaimException
import ru.astrainteractive.aspekt.module.claims.api.model.UniqueWorldKey
import ru.astrainteractive.aspekt.module.claims.api.util.findForeignOwnerUuid
import ru.astrainteractive.aspekt.module.claims.api.util.uniqueWorldKey
import ru.astrainteractive.aspekt.module.claims.api.util.updateFlag
import java.util.UUID

internal class SetChunkFlagUseCase(
    private val claimsRepository: ClaimsRepository
) {
    suspend operator fun invoke(
        uuid: UUID,
        flag: ChunkFlag,
        value: Boolean,
        key: UniqueWorldKey
    ): Result<Unit> {
        val ownerUuid = claimsRepository.findOwnerUuid(key)
        val foreignOwnerUuid = findForeignOwnerUuid(ownerUuid, uuid)
        if (foreignOwnerUuid != null) return Result.failure(UnderClaimException(foreignOwnerUuid))
        val actualChunk = claimsRepository.requireKrate(uuid)
            .getValue()
            .chunks
            .values
            .firstOrNull { claimChunk -> claimChunk.uniqueWorldKey == key }
            ?: return Result.failure(ClaimNotFoundException)
        val updatedChunk = actualChunk.updateFlag(flag, value)
        return claimsRepository.saveChunk(uuid, updatedChunk)
    }
}
