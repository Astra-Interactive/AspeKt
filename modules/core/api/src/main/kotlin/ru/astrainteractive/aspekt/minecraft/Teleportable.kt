package ru.astrainteractive.aspekt.minecraft

import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import java.util.ServiceLoader

fun interface Teleportable {
    fun teleport(location: Location)

    interface Factory<T : Any> : ServiceStatusProvider {
        fun from(instance: T): Teleportable
    }
}

fun OnlineMinecraftPlayer.asTeleportable(): Teleportable {
    return ServiceLoader.load(Teleportable.Factory::class.java)
        .filterIsInstance<Teleportable.Factory<OnlineMinecraftPlayer>>()
        .filter(ServiceStatusProvider::isReady)
        .firstOrNull()
        ?.from(this)
        ?: error("#asTeleportable could not get service")
}
