package ru.astrainteractive.aspekt.module.jail.data

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate

internal interface CachedJailApi {
    fun isInJail(uuid: String): Boolean
    fun cache(uuid: String)
    fun forget(uuid: String)

    fun getJails(): List<Jail>
    fun getInmates(): List<JailInmate>
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
