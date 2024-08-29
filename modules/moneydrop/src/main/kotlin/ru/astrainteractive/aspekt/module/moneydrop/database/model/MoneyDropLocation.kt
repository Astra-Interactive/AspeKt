package ru.astrainteractive.aspekt.module.moneydrop.database.model

import kotlinx.serialization.Serializable

@Serializable
internal data class MoneyDropLocation(
    val x: Int,
    val y: Int,
    val z: Int,
    val world: String
)
