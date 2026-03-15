package ru.astrainteractive.aspekt.module.sethome.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.server.location.KLocation

@Serializable
data class PlayerHome(
    val name: String,
    val location: KLocation
)
