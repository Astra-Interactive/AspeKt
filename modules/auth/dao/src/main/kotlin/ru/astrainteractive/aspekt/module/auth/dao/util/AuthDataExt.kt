package ru.astrainteractive.aspekt.module.auth.dao.util

import ru.astrainteractive.aspekt.module.auth.dao.model.AuthData

fun AuthData.hasSamePasswordAs(other: AuthData): Boolean {
    return passwordSha256 == other.passwordSha256
}
