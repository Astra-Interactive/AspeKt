package ru.astrainteractive.aspekt.module.adminprivate.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminPrivateConfig(
    val chunks: Map<String, AdminChunk> = emptyMap(),
    val isEnabled: Boolean = true
)
