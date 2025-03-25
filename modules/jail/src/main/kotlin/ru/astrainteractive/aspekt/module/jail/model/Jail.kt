package ru.astrainteractive.aspekt.module.jail.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Jail(
    val name: String,
    val location: JailLocation,
) {
}