package ru.astrainteractive.aspekt.module.antiswear.data

import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

internal interface SwearRepository {
    suspend fun rememberPlayer(player: OnlineKPlayer)
    suspend fun forgetPlayer(player: OnlineKPlayer)
    suspend fun setSwearFilterEnabled(player: OnlineKPlayer, isEnabled: Boolean)
    fun isSwearFilterEnabled(player: OnlineKPlayer): Boolean
    fun clear()
}
