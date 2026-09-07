package ru.astrainteractive.aspekt.module.sethome.command

import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

// sethome <home> [force]
// home <home>
// delhome <home>
internal sealed interface HomeCommand {
    data class SetHome(
        val playerData: OnlineKPlayer,
        val playerHome: PlayerHome,
        /** Set by the trailing `force` literal: overwrite a home of the same name instead of refusing. */
        val force: Boolean
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
