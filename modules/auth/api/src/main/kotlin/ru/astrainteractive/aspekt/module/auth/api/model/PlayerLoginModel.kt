package ru.astrainteractive.aspekt.module.auth.api.model

import java.util.UUID

data class PlayerLoginModel(
    val username: String,
    val uuid: UUID,
    val ip: String
)
