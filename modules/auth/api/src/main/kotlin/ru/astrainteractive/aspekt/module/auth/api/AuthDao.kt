package ru.astrainteractive.aspekt.module.auth.api

import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import java.util.UUID

interface AuthDao {
    suspend fun createAccount(authData: AuthData): Result<Unit>
    suspend fun deleteAccount(uuid: UUID): Result<Unit>
    suspend fun updateAccount(authData: AuthData): Result<Unit>
    suspend fun getUser(uuid: UUID): Result<AuthData>
    suspend fun getUser(username: String): Result<AuthData>
}

suspend fun AuthDao.checkAuthDataIsValid(authData: AuthData): Result<Boolean> {
    return getUser(authData.uuid).map { it.passwordSha256 == authData.passwordSha256 }
}

suspend fun AuthDao.isRegistered(uuid: UUID): Boolean {
    return getUser(uuid).getOrNull() != null
}
