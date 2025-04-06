package ru.astrainteractive.aspekt.module.adminprivate.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.aspekt.module.adminprivate.serialization.UuidSerializer
import java.util.UUID

@Serializable
data class ClaimPlayer(
    @Serializable(UuidSerializer::class)
    val uuid: UUID,
    val username: String
)