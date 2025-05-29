package ru.astrainteractive.aspekt.module.tpa.command

import ru.astrainteractive.astralibs.server.player.OnlineMinecraftPlayer

sealed interface TpaCommand {
    data class TpaTo(
        val executorPlayer: OnlineMinecraftPlayer,
        val targetPlayer: OnlineMinecraftPlayer
    ) : TpaCommand

    data class TpaHere(
        val executorPlayer: OnlineMinecraftPlayer,
        val targetPlayer: OnlineMinecraftPlayer
    ) : TpaCommand

    data class TpaDeny(val executorPlayer: OnlineMinecraftPlayer) : TpaCommand

    data class TpaCancel(val executorPlayer: OnlineMinecraftPlayer) : TpaCommand

    data class TpaAccept(val executorPlayer: OnlineMinecraftPlayer) : TpaCommand
}
