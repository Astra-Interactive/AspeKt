package ru.astrainteractive.aspekt.module.economy.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PlayerModel(
    val name: String,
    val uuid: String
)
