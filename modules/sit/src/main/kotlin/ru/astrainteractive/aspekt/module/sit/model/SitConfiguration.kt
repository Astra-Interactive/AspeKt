package ru.astrainteractive.aspekt.module.sit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SitConfiguration(
    @SerialName("is_enabled")
    val isEnabled: Boolean = true
)
