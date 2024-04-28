package ru.astrainteractive.aspekt.module.newbee.util

import org.bukkit.Statistic
import org.bukkit.entity.Player
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal object NewBeeExt {
    private const val TICKS_IN_SECOND = 20

    /**
     * After this duration player will not be newbee
     */
    private val MAX_DURATION_FOR_NEW_BEE = 50.minutes

    val Player.playDuration: Duration
        get() {
            val ticksPlayed = getStatistic(Statistic.PLAY_ONE_MINUTE)
            return (ticksPlayed / TICKS_IN_SECOND).seconds
        }

    val Player.newBeeShieldDurationLeft: Duration
        get() {
            println("MAX_DURATION_FOR_NEW_BEE: $MAX_DURATION_FOR_NEW_BEE; playDuration: $playDuration")
            return MAX_DURATION_FOR_NEW_BEE - playDuration
        }

    val Player.isNewBee: Boolean
        get() = playDuration < MAX_DURATION_FOR_NEW_BEE

    val Duration.ticks: Int
        get() = (inWholeSeconds * TICKS_IN_SECOND).toInt()
}
