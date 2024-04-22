package ru.astrainteractive.aspekt.module.newbee.util

import org.bukkit.Statistic
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal object NewBeeExt {
    /**
     * After this duration player will not be newbee
     */
    private val MAX_DURATION_FOR_NEW_BEE = 50.minutes

    val Player.isNewBee: Boolean
        get() {
            val ticksPlayed = getStatistic(Statistic.PLAY_ONE_MINUTE)
            val minutesPlayer = (ticksPlayed / 20).seconds
            return minutesPlayer < MAX_DURATION_FOR_NEW_BEE
        }
}
