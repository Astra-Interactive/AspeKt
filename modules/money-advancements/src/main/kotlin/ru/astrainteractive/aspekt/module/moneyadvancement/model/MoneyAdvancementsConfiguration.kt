package ru.astrainteractive.aspekt.module.moneyadvancement.model

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoneyAdvancementsConfiguration(
    @SerialName("challenge")
    @YamlComment("Money given for challenge advancemetn")
    val challenge: Int = 5000,
    @SerialName("goal")
    @YamlComment("Money given long-road advancement")
    val goal: Int = 1000,
    @SerialName("task")
    @YamlComment("Money given for default task")
    val task: Int = 1000,
    @SerialName("currency_id")
    val currencyId: String? = null
)