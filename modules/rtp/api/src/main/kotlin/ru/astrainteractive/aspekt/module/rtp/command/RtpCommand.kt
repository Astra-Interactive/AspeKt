package ru.astrainteractive.aspekt.module.rtp.command

import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

class RtpCommand(
    val player: OnlineKPlayer,
    val nextTickTime: Double
)
