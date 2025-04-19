package ru.astrainteractive.aspekt.module.sethome.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.aspekt.minecraft.location.Location

@Serializable
data class PlayerHome(
    val name: String,
    val location: Location
)
