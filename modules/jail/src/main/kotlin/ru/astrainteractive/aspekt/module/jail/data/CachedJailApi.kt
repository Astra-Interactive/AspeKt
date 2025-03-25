package ru.astrainteractive.aspekt.module.jail.data

import org.bukkit.entity.Player

internal interface CachedJailApi {
    fun isInJail(uuid: String): Boolean
    fun cache(uuid: String): Boolean
    fun forget(uuid: String): Boolean
}

internal fun CachedJailApi.isInJail(player: Player): Boolean {
    return isInJail(player.uniqueId.toString())
}

internal fun CachedJailApi.cache(player: Player): Boolean {
    return cache(player.uniqueId.toString())
}

internal fun CachedJailApi.forget(player: Player): Boolean {
    return forget(player.uniqueId.toString())
}
