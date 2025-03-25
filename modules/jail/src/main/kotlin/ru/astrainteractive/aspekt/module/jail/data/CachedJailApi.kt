package ru.astrainteractive.aspekt.module.jail.data

import org.bukkit.entity.Player

internal interface CachedJailApi {
    fun isInJail(uuid: String): Boolean
    fun cache(uuid: String)
    fun forget(uuid: String)
}

internal fun CachedJailApi.isInJail(player: Player): Boolean {
    return isInJail(player.uniqueId.toString())
}

internal fun CachedJailApi.cache(player: Player) {
    return cache(player.uniqueId.toString())
}

internal fun CachedJailApi.forget(player: Player) {
    return forget(player.uniqueId.toString())
}
