package ru.astrainteractive.aspekt.module.claims.api.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.klibs.mikro.extensions.serialization.JUuidSerializer
import java.util.UUID

@Serializable
data class ClaimPlayer(
    @Serializable(JUuidSerializer::class)
    val uuid: UUID,
    val username: String
)
