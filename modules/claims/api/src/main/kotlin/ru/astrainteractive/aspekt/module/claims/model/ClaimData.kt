package ru.astrainteractive.aspekt.module.claims.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.aspekt.module.claims.serialization.UuidSerializer
import java.util.UUID

@Serializable
data class ClaimData(
    @Serializable(UuidSerializer::class)
    val ownerUUID: UUID,
    val chunks: Map<UniqueWorldKey, ClaimChunk> = emptyMap(),
    val members: Set<ClaimPlayer> = emptySet()
)
