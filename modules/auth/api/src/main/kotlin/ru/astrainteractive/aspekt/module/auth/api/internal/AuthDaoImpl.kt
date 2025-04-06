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
import java.time.LocalDateTime
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
                    it[UserTable.createdAt] = LocalDateTime.now()
                    it[UserTable.updatedAt] = LocalDateTime.now()
                    it[UserTable.lastIpAddress] = authData.lastIpAddress
                }
            }
        }
    }

    override suspend fun deleteAccount(uuid: UUID): Result<Unit> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.deleteWhere {
                    UserTable.id eq uuid.toString()
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
                        it[UserTable.updatedAt] = LocalDateTime.now()
                        it[UserTable.lastIpAddress] = authData.lastIpAddress
                    }
                )
            }
        }
    }

    override suspend fun getUser(uuid: UUID): Result<AuthData> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.selectAll()
                    .where { UserTable.id eq uuid.toString() }
                    .map {
                        AuthData(
                            lastUsername = it[UserTable.lastUsername],
                            uuid = UUID.fromString(it[UserTable.id].value),
                            passwordSha256 = it[UserTable.passwordHash],
                            lastIpAddress = it[UserTable.lastIpAddress]
                        )
                    }
                    .first()
            }
        }
    }

    override suspend fun getUser(username: String): Result<AuthData> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.selectAll()
                    .where { UserTable.lastUsername eq username }
                    .map {
                        AuthData(
                            lastUsername = it[UserTable.lastUsername],
                            uuid = UUID.fromString(it[UserTable.id].value),
                            passwordSha256 = it[UserTable.passwordHash],
                            lastIpAddress = it[UserTable.lastIpAddress]
                        )
                    }
                    .first()
            }
        }
    }
}
