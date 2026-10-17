package ru.astrainteractive.aspekt.module.playtimereward.api

import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeReward
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.MutableKrate

internal interface PlaytimeRewardRepository {
    fun krateOf(player: OnlineKPlayer): MutableKrate<PlaytimeReward>
}
