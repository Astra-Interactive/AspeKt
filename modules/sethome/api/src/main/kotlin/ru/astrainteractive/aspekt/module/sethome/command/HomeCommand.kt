package ru.astrainteractive.aspekt.module.sethome.command

import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.astralibs.server.player.OnlineMinecraftPlayer

// sethome <home>
// home <home>
// delhome <home>
sealed interface HomeCommand {
    data class SetHome(
        val playerData: OnlineMinecraftPlayer,
        val playerHome: PlayerHome
    ) : HomeCommand

    data class DelHome(
        val playerData: OnlineMinecraftPlayer,
        val homeName: String
    ) : HomeCommand

    data class TpHome(
        val playerData: OnlineMinecraftPlayer,
        val homeName: String
    ) : HomeCommand
}
