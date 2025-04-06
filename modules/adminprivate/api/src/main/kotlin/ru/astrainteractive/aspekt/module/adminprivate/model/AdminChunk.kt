package ru.astrainteractive.aspekt.module.adminprivate.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminChunk(
    val x: Int,
    val z: Int,
    val worldName: String,
    val flags: Map<ChunkFlag, Boolean>,
    val chunkKey: Long,
)
