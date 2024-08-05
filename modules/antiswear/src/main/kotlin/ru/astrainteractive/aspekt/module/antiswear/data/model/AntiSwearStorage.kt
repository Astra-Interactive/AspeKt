package ru.astrainteractive.aspekt.module.antiswear.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AntiSwearStorage(
    val playerName: String,
    val uuid: String,
    val isSwearFilterEnabled: Boolean = true
)
