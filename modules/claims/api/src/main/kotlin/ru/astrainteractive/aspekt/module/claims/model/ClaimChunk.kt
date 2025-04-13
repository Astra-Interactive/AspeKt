package ru.astrainteractive.aspekt.module.claims.model

import kotlinx.serialization.Serializable

@Serializable
data class ClaimChunk(
    val x: Int,
    val z: Int,
    val worldName: String,
    val flags: Map<ChunkFlag, Boolean>,
    val chunkKey: Long,
)
