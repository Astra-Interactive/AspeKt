package ru.astrainteractive.aspekt.module.rtp.api.command

import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

internal class RtpCommand(
    val player: OnlineKPlayer,
    val ticksPerSecond: Double
)
