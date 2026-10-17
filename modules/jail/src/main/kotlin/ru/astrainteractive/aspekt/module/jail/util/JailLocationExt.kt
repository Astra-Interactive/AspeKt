package ru.astrainteractive.aspekt.module.jail.util

import org.bukkit.Location
import ru.astrainteractive.aspekt.module.jail.model.JailLocation
import ru.astrainteractive.astralibs.server.location.KLocation

internal fun Location.toJailLocation(): JailLocation {
    return JailLocation(
        world.name,
        x,
        y,
        z
    )
}

internal fun KLocation.toJailLocation(): JailLocation {
    return JailLocation(
        worldName,
        x,
        y,
        z
    )
}

internal fun JailLocation.toKLocation(): KLocation {
    return KLocation(
        x = x,
        y = y,
        z = z,
        worldName = world
    )
}
