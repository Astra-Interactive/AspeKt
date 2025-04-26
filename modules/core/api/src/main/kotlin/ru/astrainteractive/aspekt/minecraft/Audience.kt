package ru.astrainteractive.aspekt.minecraft

import net.kyori.adventure.text.Component
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import java.util.ServiceLoader

fun interface Audience {
    fun sendMessage(component: Component)

    interface Factory<T : Any> : ServiceStatusProvider {
        fun from(instance: T): Audience
    }
}

fun OnlineMinecraftPlayer.asAudience(): Audience {
    return ServiceLoader.load(Audience.Factory::class.java)
        .filterIsInstance<Audience.Factory<OnlineMinecraftPlayer>>()
        .filter(ServiceStatusProvider::isReady)
        .firstOrNull()
        ?.from(this)
        ?: error("#asAudience could not get service")
}
