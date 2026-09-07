package ru.astrainteractive.aspekt.module.sethome.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SetHomeConfiguration(
    @SerialName("is_enabled")
    val isEnabled: Boolean = false,
    /** Server-wide default; a player holding `aspekt.sethome.<count>` uses that count instead. */
    @SerialName("max_homes")
    val maxHomes: Int = 1
)
