package ru.astrainteractive.aspekt.module.sethome.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeLocation(
    val x: Double,
    val y: Double,
    val z: Double,
    val worldName: String,
)
