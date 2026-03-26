package ru.astrainteractive.aspekt.module.moneydrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoneyDropConfiguration(
    @SerialName("money_drop")
    val moneyDrop: Map<String, MoneyDropEntry> = emptyMap(),
) {
    @Serializable
    data class MoneyDropEntry(
        val from: String,
        val chance: Double,
        val min: Double,
        val max: Double,
        val currencyId: String? = null
    )
}