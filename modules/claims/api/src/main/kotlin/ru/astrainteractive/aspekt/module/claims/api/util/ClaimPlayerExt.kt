@file:Suppress("UtilFileName")

package ru.astrainteractive.aspekt.module.claims.api.util

import ru.astrainteractive.aspekt.module.claims.api.model.ClaimPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

internal fun OnlineKPlayer.toClaimPlayer(): ClaimPlayer = ClaimPlayer(uuid = uuid, username = name)
