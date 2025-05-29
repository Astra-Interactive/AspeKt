package ru.astrainteractive.aspekt.util

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.minecraft.player.OfflineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

fun Player.asOnlineMinecraftPlayer(): OnlineMinecraftPlayer {
    return OnlineMinecraftPlayer(
        uuid = uniqueId,
        name = name,
        ipAddress = address?.hostName.orEmpty()
    )
}

fun OfflinePlayer.asOfflineMinecraftPlayer(): OfflineMinecraftPlayer {
    return OfflineMinecraftPlayer(
        uuid = uniqueId,
    )
}
