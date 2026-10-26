package ru.astrainteractive.aspekt.module.playtimereward.util

import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeReward
import kotlin.time.Duration
import kotlin.time.Instant

internal fun PlaytimeReward.joinedAt(now: Instant): PlaytimeReward = copy(latestJoin = now)

internal fun PlaytimeReward.countedUntil(now: Instant): PlaytimeReward {
    return copy(
        latestJoin = now,
        timePlayed = timePlayed + (now - latestJoin)
    )
}

internal fun PlaytimeReward.isRewardDue(requiredDuration: Duration): Boolean {
    return timePlayed >= requiredDuration
}
