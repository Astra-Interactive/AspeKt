package ru.astrainteractive.aspekt.module.claims.util

import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer

fun ClaimPlayer.toOnlineMinecraftPlayer() = OnlineMinecraftPlayer(
    uuid = uuid,
    name = username
)
