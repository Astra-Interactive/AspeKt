package ru.astrainteractive.aspekt.module.claims.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.aspekt.module.claims.serialization.UuidSerializer
import java.util.UUID

@Serializable
data class ClaimPlayer(
    @Serializable(UuidSerializer::class)
    val uuid: UUID,
    val username: String
)
