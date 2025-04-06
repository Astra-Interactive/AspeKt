package ru.astrainteractive.aspekt.module.adminprivate.model

import kotlinx.serialization.Serializable

@Serializable
data class ClaimData(
    val chunks: Map<UniqueWorldKey, ClaimChunk> = emptyMap(),
    val members: List<ClaimPlayer> = emptyList()
)
