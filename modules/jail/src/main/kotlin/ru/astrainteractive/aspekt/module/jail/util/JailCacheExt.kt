package ru.astrainteractive.aspekt.module.jail.util

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.jail.api.JailCache

internal fun JailCache.isInJail(player: Player): Boolean {
    return isInJail(player.uniqueId.toString())
}

internal fun JailCache.cache(player: Player) {
    return cache(player.uniqueId.toString())
}

internal fun JailCache.forget(player: Player) {
    return forget(player.uniqueId.toString())
}
