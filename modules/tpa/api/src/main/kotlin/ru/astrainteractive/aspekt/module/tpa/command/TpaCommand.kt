package ru.astrainteractive.aspekt.module.tpa.command

import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

sealed interface TpaCommand {
    data class TpaTo(
        val executorPlayer: OnlineKPlayer,
        val targetPlayer: OnlineKPlayer
    ) : TpaCommand

    data class TpaHere(
        val executorPlayer: OnlineKPlayer,
        val targetPlayer: OnlineKPlayer
    ) : TpaCommand

    data class TpaDeny(val executorPlayer: OnlineKPlayer) : TpaCommand

    data class TpaCancel(val executorPlayer: OnlineKPlayer) : TpaCommand

    data class TpaAccept(val executorPlayer: OnlineKPlayer) : TpaCommand
}
