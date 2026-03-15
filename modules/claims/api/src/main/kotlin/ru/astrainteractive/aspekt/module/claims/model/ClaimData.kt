package ru.astrainteractive.aspekt.module.claims.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.klibs.mikro.extensions.serialization.JUuidSerializer
import java.util.UUID

@Serializable
data class ClaimData(
    @Serializable(JUuidSerializer::class)
    val ownerUUID: UUID,
    val chunks: Map<UniqueWorldKey, ClaimChunk> = emptyMap(),
    val members: Set<ClaimPlayer> = emptySet()
)
