package ru.astrainteractive.aspekt.module.jail.util

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import java.util.UUID

internal fun OfflinePlayer.sendMessage(message: Component) {
    Bukkit.getPlayer(uniqueId)?.sendMessage(message)
}

internal val JailInmate.offlinePlayer: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(UUID.fromString(uuid))
