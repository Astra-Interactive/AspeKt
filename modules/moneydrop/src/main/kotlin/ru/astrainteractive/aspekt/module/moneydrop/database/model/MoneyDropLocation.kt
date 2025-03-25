package ru.astrainteractive.aspekt.module.moneydrop.database.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.aspekt.module.moneydrop.database.serialization.InstantSerializer
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
