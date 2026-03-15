package ru.astrainteractive.aspekt.module.sethome.command

import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

// sethome <home>
// home <home>
// delhome <home>
sealed interface HomeCommand {
    data class SetHome(
        val playerData: OnlineKPlayer,
        val playerHome: PlayerHome
    ) : HomeCommand

    data class DelHome(
        val playerData: OnlineKPlayer,
        val homeName: String
    ) : HomeCommand

    data class TpHome(
        val playerData: OnlineKPlayer,
        val homeName: String
    ) : HomeCommand
}
