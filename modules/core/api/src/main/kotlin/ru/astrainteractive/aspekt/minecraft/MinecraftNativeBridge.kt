package ru.astrainteractive.aspekt.minecraft

import ru.astrainteractive.aspekt.minecraft.player.MinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.astralibs.permission.Permissible
import java.util.UUID

interface MinecraftNativeBridge {
    fun OnlineMinecraftPlayer.asAudience(): Audience
    fun OnlineMinecraftPlayer.asLocatable(): Locatable
    fun OnlineMinecraftPlayer.asTeleportable(): Teleportable
    fun MinecraftPlayer.asPermissible(): Permissible
    fun findPlayer(uuid: UUID): MinecraftPlayer?
}
