package ru.astrainteractive.aspekt.module.auth.api.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.StringFormat
import org.jetbrains.exposed.v1.core.Slf4jSqlDebugLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.internal.AuthDaoImpl
import ru.astrainteractive.aspekt.module.auth.api.internal.AuthorizedApiImpl
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.api.table.UserTable
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.parseOrWriteIntoDefault
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import ru.astrainteractive.klibs.mikro.exposed.util.connectAsFlow
import java.io.File

class AuthApiModule(
    ioScope: CoroutineScope,
    private val dataFolder: File,
    private val stringFormat: StringFormat
) {
    private val moduleIoScope = CoroutineScope(ioScope.coroutineContext + SupervisorJob())

    private val databaseFlow: Flow<Database> = DatabaseConfiguration
        .H2(dataFolder.resolve("auth_database").path)
        .connectAsFlow()
        .onEach { database ->
            TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
            transaction(database) {
                addLogger(Slf4jSqlDebugLogger)
                SchemaUtils.create(
                    UserTable,
                )
            }
        }
        .shareIn(moduleIoScope, SharingStarted.Lazily, replay = 1)

    val authDao: AuthDao = AuthDaoImpl(
        databaseFlow = databaseFlow
    )
    val authorizedApi: AuthorizedApi = AuthorizedApiImpl(
        authDao = authDao,
        scope = moduleIoScope
    )
    val translationKrate = DefaultMutableKrate(
        factory = ::AuthTranslation,
        loader = {
            stringFormat.parseOrWriteIntoDefault(
                file = dataFolder.resolve("translation.yml"),
                default = ::AuthTranslation
            )
        }
    ).asCachedKrate()

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onReload = {
            translationKrate.getValue()
        },
        onDisable = {
            moduleIoScope.cancel()
        }
    )

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("api.yml")
    }
}
