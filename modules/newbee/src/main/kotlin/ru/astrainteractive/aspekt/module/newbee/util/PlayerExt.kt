package ru.astrainteractive.aspekt.module.newbee.util

import org.bukkit.Statistic
import org.bukkit.entity.Player
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val TICKS_IN_SECOND = 20

private val MAX_DURATION_FOR_NEW_BEE = 50.minutes

internal val Player.playDuration: Duration
    get() {
        val ticksPlayed = getStatistic(Statistic.PLAY_ONE_MINUTE)
        return (ticksPlayed / TICKS_IN_SECOND).seconds
    }

internal val Player.newBeeShieldDurationLeft: Duration
    get() = MAX_DURATION_FOR_NEW_BEE - playDuration

internal val Player.isNewBee: Boolean
    get() = playDuration < MAX_DURATION_FOR_NEW_BEE
