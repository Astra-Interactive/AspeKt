package ru.astrainteractive.aspekt.module.rtp.command

import ru.astrainteractive.astralibs.server.player.OnlineMinecraftPlayer

class RtpCommand(
    val player: OnlineMinecraftPlayer,
    val nextTickTime: Double
)
