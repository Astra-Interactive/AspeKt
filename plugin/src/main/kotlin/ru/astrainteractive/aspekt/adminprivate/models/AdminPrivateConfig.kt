package ru.astrainteractive.aspekt.adminprivate.models

import kotlinx.serialization.Serializable

@Serializable
data class AdminPrivateConfig(
    val chunks: Map<String, AdminChunk> = emptyMap(),
    val isEnabled: Boolean = true
)
