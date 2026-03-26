package ru.astrainteractive.aspekt.module.autocrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutoCropConfiguration(
    @SerialName("enabled")
    val enabled: Boolean = true,
    @SerialName("min")
    val min: Int = 0,
    @SerialName("max")
    val max: Int = 0,
)