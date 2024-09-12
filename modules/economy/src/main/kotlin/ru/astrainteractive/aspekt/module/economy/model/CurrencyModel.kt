package ru.astrainteractive.aspekt.module.economy.model

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CurrencyModel(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("priority")
    @YamlComment(
        "Priority is required to define priority of currency",
        "The higher the number - the higher the priority",
        "Currency with most priority will be used by most plugins",
        "The range if [0,4]"
    )
    val priority: Int = 0
)
