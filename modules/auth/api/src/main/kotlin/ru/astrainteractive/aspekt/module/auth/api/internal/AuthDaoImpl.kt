package ru.astrainteractive.aspekt.module.auth.api.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import ru.astrainteractive.aspekt.module.auth.api.table.UserTable
import java.util.UUID

internal class AuthDaoImpl(private val databaseFlow: Flow<Database>) : AuthDao {
    private suspend fun requireDatabase() = databaseFlow.first()
    override suspend fun createAccount(authData: AuthData): Result<Unit> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.insert {
                    it[UserTable.id] = authData.uuid.toString()
                    it[UserTable.lastUsername] = authData.lastUsername
                    it[UserTable.passwordHash] = authData.passwordSha256
                }
            }
        }
    }

    override suspend fun deleteAccount(authData: AuthData): Result<Unit> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.deleteWhere {
                    UserTable.id eq authData.uuid.toString()
                }
            }
        }
    }

    override suspend fun updateAccount(authData: AuthData): Result<Unit> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.update(
                    where = {
                        UserTable.id eq authData.uuid.toString()
                    },
                    body = {
                        it[UserTable.passwordHash] = authData.passwordSha256
                        it[UserTable.lastUsername] = authData.lastUsername
                    }
                )
            }
        }
    }

    override suspend fun isRegistered(uuid: UUID): Result<Boolean> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.selectAll()
                    .where { UserTable.id eq uuid.toString() }
                    .firstOrNull() != null
            }
        }
    }

    override suspend fun checkAuthDataIsOk(authData: AuthData): Result<Boolean> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.selectAll()
                    .where { UserTable.id eq authData.uuid.toString() }
                    .map {
                        AuthData(
                            lastUsername = it[UserTable.lastUsername],
                            uuid = UUID.fromString(it[UserTable.id].value),
                            passwordSha256 = it[UserTable.passwordHash]
                        )
                    }
                    .firstOrNull()
                    ?.passwordSha256 == authData.passwordSha256
            }
        }
    }
}
