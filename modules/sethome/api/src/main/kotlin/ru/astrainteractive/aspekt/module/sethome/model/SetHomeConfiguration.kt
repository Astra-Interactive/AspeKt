package ru.astrainteractive.aspekt.module.sethome.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SetHomeConfiguration(
    @SerialName("is_enabled")
    val isEnabled: Boolean = false
)
