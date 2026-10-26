package ru.astrainteractive.aspekt.module.auth.dao.api

import ru.astrainteractive.aspekt.module.auth.dao.model.AuthData
import java.util.UUID

interface AuthRepository {
    suspend fun createAccount(authData: AuthData): Result<Unit>
    suspend fun deleteAccount(uuid: UUID): Result<Unit>
    suspend fun updateAccount(authData: AuthData): Result<Unit>
    suspend fun findUser(uuid: UUID): Result<AuthData>
    suspend fun findUser(username: String): Result<AuthData>
}
