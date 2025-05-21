package ru.astrainteractive.aspekt.minecraft

import ru.astrainteractive.aspekt.minecraft.location.Location

fun interface Teleportable {
    fun teleport(location: Location)

    interface Factory<T : Any> {
        fun from(instance: T): Teleportable
    }
}
