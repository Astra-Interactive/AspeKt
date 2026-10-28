package ru.astrainteractive.aspekt.module.auth.exposed.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import org.jetbrains.exposed.v1.core.Slf4jSqlDebugLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import ru.astrainteractive.aspekt.module.auth.exposed.account.ExposedAuthRepository
import ru.astrainteractive.aspekt.module.auth.exposed.table.UserTable
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import ru.astrainteractive.klibs.mikro.exposed.util.connectAsFlow
import java.io.File
import java.sql.Connection

class AuthExposedModule(
    ioScope: CoroutineScope,
    dataFolder: File
) {
    private val moduleIoScope = CoroutineScope(ioScope.coroutineContext + SupervisorJob())

    private val databaseFlow: Flow<Database> = DatabaseConfiguration
        .H2(dataFolder.resolve("auth_database").path)
        .connectAsFlow()
        .onEach { database ->
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            transaction(database) {
                addLogger(Slf4jSqlDebugLogger)
                SchemaUtils.create(UserTable)
            }
        }
        .shareIn(
            scope = moduleIoScope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    val authRepository: AuthRepository = ExposedAuthRepository(databaseFlow = databaseFlow)

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = {
            moduleIoScope.cancel()
        }
    )
}
