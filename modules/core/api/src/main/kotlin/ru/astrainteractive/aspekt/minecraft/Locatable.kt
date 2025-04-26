package ru.astrainteractive.aspekt.minecraft

import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import java.util.ServiceLoader

fun interface Locatable {
    fun getLocation(): Location

    interface Factory<T : Any> : ServiceStatusProvider {
        fun from(instance: T): Locatable
    }
}

fun OnlineMinecraftPlayer.asLocatable(): Locatable {
    return ServiceLoader.load(Locatable.Factory::class.java)
        .filterIsInstance<Locatable.Factory<OnlineMinecraftPlayer>>()
        .filter(ServiceStatusProvider::isReady)
        .firstOrNull()
        ?.from(this)
        ?: error("#asLocatable could not get service")
}
