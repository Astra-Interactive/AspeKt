package ru.astrainteractive.aspekt.minecraft.teleport

import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import java.util.ServiceLoader

fun interface Teleportable {
    fun teleport(location: Location)

    interface Factory<T : Any> {
        fun from(instance: T): Teleportable
    }

    object Stub : Teleportable,
        Logger by JUtiltLogger("AspeKt-Teleprotable") {
        override fun teleport(location: Location) {
            error { "#teleport using stub teleportable. Check your services" }
        }
    }
}

fun OnlineMinecraftPlayer.asTeleportable() {
    ServiceLoader.load(Teleportable.Factory::class.java)
        .filterIsInstance<Teleportable.Factory<OnlineMinecraftPlayer>>()
        .firstOrNull()
        ?.from(this)
        ?: Teleportable.Stub
}

