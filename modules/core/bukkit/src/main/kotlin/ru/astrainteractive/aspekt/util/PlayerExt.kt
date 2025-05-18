package ru.astrainteractive.aspekt.util

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

fun Player.asOnlineMinecraftPlayer(): OnlineMinecraftPlayer {
    return OnlineMinecraftPlayer(
        uuid = uniqueId,
        name = name,
        ipAddress = address?.hostName.orEmpty()
    )
}