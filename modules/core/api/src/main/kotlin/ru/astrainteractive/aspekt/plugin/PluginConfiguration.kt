package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginConfiguration(
    @SerialName("sit")
    val sit: Boolean = true,
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
