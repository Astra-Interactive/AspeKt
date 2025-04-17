package ru.astrainteractive.aspekt.module.sethome.command

import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome

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
