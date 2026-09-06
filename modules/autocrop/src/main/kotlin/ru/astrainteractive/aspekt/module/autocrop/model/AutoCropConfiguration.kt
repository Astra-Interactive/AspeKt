package ru.astrainteractive.aspekt.module.autocrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AutoCropConfiguration(
    @SerialName("is_enabled")
    val isEnabled: Boolean = false,
    @SerialName("min")
    val min: Int = 0,
    @SerialName("max")
    val max: Int = 0,
)
