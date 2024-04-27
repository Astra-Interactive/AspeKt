package ru.astrainteractive.aspekt.module.antiswear.model

import kotlinx.serialization.Serializable

@Serializable
data class AntiSwearStorage(
    val playerName: String,
    val uuid: String,
    val isSwearFilterEnabled: Boolean = true
)
