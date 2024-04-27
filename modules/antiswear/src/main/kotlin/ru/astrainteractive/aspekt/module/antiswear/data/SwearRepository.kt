package ru.astrainteractive.aspekt.module.antiswear.data

import org.bukkit.entity.Player

internal interface SwearRepository {
    suspend fun rememberPlayer(player: Player)
    suspend fun forgetPlayer(player: Player)
    suspend fun setSwearFilterEnabled(player: Player, isEnabled: Boolean)
    fun isSwearFilterEnabled(player: Player): Boolean
}
