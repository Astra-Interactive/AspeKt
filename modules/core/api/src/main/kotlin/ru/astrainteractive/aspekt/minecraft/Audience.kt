package ru.astrainteractive.aspekt.minecraft

import net.kyori.adventure.text.Component

fun interface Audience {
    fun sendMessage(component: Component)

    interface Factory<T : Any> {
        fun from(instance: T): Audience
    }
}
