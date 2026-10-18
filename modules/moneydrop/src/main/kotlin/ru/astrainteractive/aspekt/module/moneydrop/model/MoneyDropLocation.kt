package ru.astrainteractive.aspekt.module.moneydrop.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
internal data class MoneyDropLocation(
    val x: Int,
    val y: Int,
    val z: Int,
    val world: String,
    val additionalConstraint: String? = null,
    @Serializable(InstantSerializer::class)
    val instant: Instant
)
