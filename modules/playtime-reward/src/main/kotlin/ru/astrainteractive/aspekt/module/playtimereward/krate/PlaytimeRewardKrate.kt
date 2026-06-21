package ru.astrainteractive.aspekt.module.playtimereward.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeReward
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.MutableKrate
import java.io.File
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal interface PlaytimeRewardKrate : MutableKrate<PlaytimeReward> {
    class Factory(
        private val tempDataFolder: File,
        private val stringFormat: StringFormat,
        private val clock: Clock
    ) {
        fun create(onlineKPlayer: OnlineKPlayer): PlaytimeRewardKrate {
            return object :
                PlaytimeRewardKrate,
                MutableKrate<PlaytimeReward> by stringFormat.krateOf(
                    file = tempDataFolder.resolve("playtime_${onlineKPlayer.uuid}.json"),
                    factory = {
                        PlaytimeReward(
                            timePlayed = Duration.ZERO,
                            uuid = onlineKPlayer.uuid,
                            latestUsername = onlineKPlayer.name,
                            latestJoin = clock.now()
                        )
                    }
                ) {}
        }
    }
}
