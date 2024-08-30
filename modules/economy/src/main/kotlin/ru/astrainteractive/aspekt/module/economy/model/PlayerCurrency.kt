package ru.astrainteractive.aspekt.module.economy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerCurrency(
    @SerialName("player_model")
    val playerModel: PlayerModel,
    @SerialName("amount")
    val amount: Double,
    @SerialName("currency_model")
    val currencyModel: CurrencyModel
)
