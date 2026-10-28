package ru.astrainteractive.aspekt.module.auth.dao.util

import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import ru.astrainteractive.aspekt.module.auth.dao.model.AuthData
import java.util.UUID

suspend fun AuthRepository.checkAuthDataIsValid(authData: AuthData): Result<Boolean> {
    return findUser(authData.uuid).map { storedAuthData -> storedAuthData.passwordSha256 == authData.passwordSha256 }
}

suspend fun AuthRepository.isRegistered(uuid: UUID): Boolean {
    return findUser(uuid).getOrNull() != null
}
