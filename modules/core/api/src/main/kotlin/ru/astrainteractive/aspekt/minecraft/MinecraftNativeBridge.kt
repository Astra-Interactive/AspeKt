package ru.astrainteractive.aspekt.minecraft

import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

interface MinecraftNativeBridge {
    fun OnlineMinecraftPlayer.asAudience(): Audience
    fun OnlineMinecraftPlayer.asLocatable(): Locatable
    fun OnlineMinecraftPlayer.asTeleportable(): Teleportable
}
