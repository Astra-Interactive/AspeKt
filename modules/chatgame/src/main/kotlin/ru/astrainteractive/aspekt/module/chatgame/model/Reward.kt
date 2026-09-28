package ru.astrainteractive.aspekt.module.chatgame.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.random.nextInt

@Serializable
@SerialName("REWARD")
internal sealed interface Reward {
    @SerialName("MONEY")
    @Serializable
    class Money(
        val minAmount: Double,
        val maxAmount: Double,
        @SerialName("currency_id")
        val currencyId: String? = null
    ) : Reward
}

internal fun Reward.Money.randomAmount(random: Random): Int {
    val firstBound = minAmount.roundToInt().coerceAtLeast(0)
    val secondBound = maxAmount.roundToInt().coerceAtLeast(0)
    return random.nextInt(minOf(firstBound, secondBound)..maxOf(firstBound, secondBound))
}
