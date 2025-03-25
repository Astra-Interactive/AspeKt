package ru.astrainteractive.aspekt.module.jail.util

import org.bukkit.Bukkit
import org.bukkit.Location
import ru.astrainteractive.aspekt.module.jail.model.JailLocation

internal fun JailLocation.toBukkitLocation(): Location {
    return Location(
        Bukkit.getWorld(world),
        x,
        y,
        z
    )
}

internal fun Location.toJailLocation(): JailLocation {
    return JailLocation(
        world.name,
        x,
        y,
        z
    )
}
