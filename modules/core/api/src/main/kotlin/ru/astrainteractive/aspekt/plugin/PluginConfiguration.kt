package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginConfiguration(
    @SerialName("sit")
    val sit: Boolean = true,

    ) {


}
