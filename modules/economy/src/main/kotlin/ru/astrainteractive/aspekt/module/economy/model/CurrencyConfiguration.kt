package ru.astrainteractive.aspekt.module.economy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CurrencyConfiguration(
    val currencies: Map<String, CurrencyModel> = emptyMap(),
    @SerialName("should_sync")
    val shouldSync: Boolean = false
)
