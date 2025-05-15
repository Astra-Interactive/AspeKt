package ru.astrainteractive.aspekt.minecraft

import ru.astrainteractive.aspekt.minecraft.location.Location

fun interface Locatable {
    fun getLocation(): Location

    interface Factory<T : Any> {
        fun from(instance: T): Locatable
    }
}
