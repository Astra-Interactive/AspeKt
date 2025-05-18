package ru.astrainteractive.aspekt.core.forge.util

import net.minecraft.server.level.ServerPlayer
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

fun ServerPlayer.asOnlineMinecraftPlayer(): OnlineMinecraftPlayer {
    return OnlineMinecraftPlayer(
        uuid = uuid,
        name = name.toPlain(),
        ipAddress = ipAddress
    )
}