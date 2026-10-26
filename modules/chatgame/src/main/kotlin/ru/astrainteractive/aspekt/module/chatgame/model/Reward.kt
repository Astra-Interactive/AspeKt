package ru.astrainteractive.aspekt.module.chatgame.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
