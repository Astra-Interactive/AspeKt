package ru.astrainteractive.aspekt.module.chatgame.util

import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.random.nextInt

internal fun Reward.Money.randomAmount(random: Random): Int {
    val firstBound = minAmount.roundToInt().coerceAtLeast(0)
    val secondBound = maxAmount.roundToInt().coerceAtLeast(0)
    return random.nextInt(minOf(firstBound, secondBound)..maxOf(firstBound, secondBound))
}
