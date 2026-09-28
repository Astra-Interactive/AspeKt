package ru.astrainteractive.aspekt.module.jail.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.server.util.asKAudience
import java.util.UUID

internal fun OfflinePlayer.sendMessage(message: LocalizableComponent) {
    Bukkit.getPlayer(uniqueId)?.asKAudience()?.sendMessage(message)
}

internal val JailInmate.offlinePlayer: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(UUID.fromString(uuid))
