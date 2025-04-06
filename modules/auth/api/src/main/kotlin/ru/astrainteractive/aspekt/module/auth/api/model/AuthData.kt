package ru.astrainteractive.aspekt.module.auth.api.model

import java.util.UUID

data class AuthData(
    val lastUsername: String,
    val uuid: UUID,
    val passwordSha256: String,
    val lastIpAddress: String
)
