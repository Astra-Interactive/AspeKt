package ru.astrainteractive.aspekt.module.sethome.util

import ru.astrainteractive.aspekt.core.forge.model.Location
import ru.astrainteractive.aspekt.module.sethome.model.HomeLocation

fun Location.toHomeLocation(): HomeLocation {
    return HomeLocation(
        x = x,
        y = y,
        z = z,
        worldName = worldName,
    )
}
