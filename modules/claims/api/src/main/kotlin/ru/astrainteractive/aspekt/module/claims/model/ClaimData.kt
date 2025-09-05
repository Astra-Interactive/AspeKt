package ru.astrainteractive.aspekt.module.claims.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.klibs.mikro.extensions.serialization.UUIDSerializer
import java.util.UUID

@Serializable
data class ClaimData(
    @Serializable(UUIDSerializer::class)
    val ownerUUID: UUID,
    val chunks: Map<UniqueWorldKey, ClaimChunk> = emptyMap(),
    val members: Set<ClaimPlayer> = emptySet()
)
