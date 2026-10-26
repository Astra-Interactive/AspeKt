package ru.astrainteractive.aspekt.module.auth.exposed.account

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import ru.astrainteractive.aspekt.module.auth.dao.model.AuthData
import ru.astrainteractive.aspekt.module.auth.exposed.table.UserTable
import ru.astrainteractive.klibs.mikro.core.coroutines.propagateCancellationException
import java.time.LocalDateTime
import java.util.UUID

internal class ExposedAuthRepository(private val databaseFlow: Flow<Database>) : AuthRepository {
    private suspend fun requireDatabase() = databaseFlow.first()
    override suspend fun createAccount(authData: AuthData): Result<Unit> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.insert { statement ->
                    statement[UserTable.id] = authData.uuid.toString()
                    statement[UserTable.lastUsername] = authData.lastUsername
                    statement[UserTable.passwordHash] = authData.passwordSha256
                    statement[UserTable.createdAt] = LocalDateTime.now()
                    statement[UserTable.updatedAt] = LocalDateTime.now()
                    statement[UserTable.lastIpAddress] = authData.lastIpAddress
                }
                Unit
            }
        }.propagateCancellationException()
    }

    override suspend fun deleteAccount(uuid: UUID): Result<Unit> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.deleteWhere {
                    UserTable.id eq uuid.toString()
                }
                Unit
            }
        }.propagateCancellationException()
    }

    override suspend fun updateAccount(authData: AuthData): Result<Unit> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.update(
                    where = {
                        UserTable.id eq authData.uuid.toString()
                    },
                    body = { statement ->
                        statement[UserTable.passwordHash] = authData.passwordSha256
                        statement[UserTable.lastUsername] = authData.lastUsername
                        statement[UserTable.updatedAt] = LocalDateTime.now()
                        statement[UserTable.lastIpAddress] = authData.lastIpAddress
                    }
                )
                Unit
            }
        }.propagateCancellationException()
    }

    override suspend fun findUser(uuid: UUID): Result<AuthData> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.selectAll()
                    .where { UserTable.id eq uuid.toString() }
                    .map { row ->
                        AuthData(
                            lastUsername = row[UserTable.lastUsername],
                            uuid = UUID.fromString(row[UserTable.id].value),
                            passwordSha256 = row[UserTable.passwordHash],
                            lastIpAddress = row[UserTable.lastIpAddress]
                        )
                    }
                    .first()
            }
        }.propagateCancellationException()
    }

    override suspend fun findUser(username: String): Result<AuthData> {
        return runCatching {
            transaction(requireDatabase()) {
                UserTable.selectAll()
                    .where { UserTable.lastUsername eq username }
                    .map { row ->
                        AuthData(
                            lastUsername = row[UserTable.lastUsername],
                            uuid = UUID.fromString(row[UserTable.id].value),
                            passwordSha256 = row[UserTable.passwordHash],
                            lastIpAddress = row[UserTable.lastIpAddress]
                        )
                    }
                    .first()
            }
        }.propagateCancellationException()
    }
}
