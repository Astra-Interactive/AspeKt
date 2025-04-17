package ru.astrainteractive.aspekt.module.sethome.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerHome(
    val name: String,
    val location: HomeLocation
)
