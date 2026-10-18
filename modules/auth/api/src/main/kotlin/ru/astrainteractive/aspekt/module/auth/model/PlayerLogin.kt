package ru.astrainteractive.aspekt.module.auth.model

import java.util.UUID

data class PlayerLogin(
    val username: String,
    val uuid: UUID,
    val ip: String
)
