package ru.astrainteractive.aspekt.module.economy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PlayerCurrency(
    @SerialName("name")
    val name: String,
    @SerialName("uuid")
    val uuid: String,
    @SerialName("amount")
    val amount: Double,
    @SerialName("currency_model")
    val currencyModel: CurrencyModel
)
