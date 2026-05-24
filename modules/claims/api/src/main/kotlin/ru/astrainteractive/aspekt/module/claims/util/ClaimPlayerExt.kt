package ru.astrainteractive.aspekt.module.claims.util

import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

internal fun OnlineKPlayer.toClaimPlayer(): ClaimPlayer = ClaimPlayer(uuid = uuid, username = name)
