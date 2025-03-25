package ru.astrainteractive.aspekt.module.jail.model

import kotlinx.serialization.Serializable

@Serializable
internal data class JailLocation(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double
)
