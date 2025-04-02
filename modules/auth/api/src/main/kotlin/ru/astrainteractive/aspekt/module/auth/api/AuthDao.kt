package ru.astrainteractive.aspekt.module.auth.api

import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import java.util.UUID

interface AuthDao {
    suspend fun createAccount(authData: AuthData): Result<Unit>
    suspend fun deleteAccount(authData: AuthData): Result<Unit>
    suspend fun updateAccount(authData: AuthData): Result<Unit>
    suspend fun isRegistered(uuid: UUID): Result<Boolean>
    suspend fun checkAuthDataIsOk(authData: AuthData): Result<Boolean>
}
