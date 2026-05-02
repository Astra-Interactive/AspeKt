package ru.astrainteractive.aspekt.module.auth.api.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
import ru.astrainteractive.astralibs.util.parseOrWriteIntoDefault
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.mikro.core.coroutines.mapCached
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import ru.astrainteractive.klibs.mikro.exposed.util.connect
import java.io.File

class AuthApiModule(
    ioScope: CoroutineScope,
    private val dataFolder: File,
    private val stringFormat: StringFormat
) {
    private val databaseFlow: Flow<Database> = flowOf(
        DatabaseConfiguration.H2(dataFolder.resolve("auth_database").path)
    ).mapCached(ioScope) { dbConfig, previous ->
        previous?.connector?.invoke()?.close()
        previous?.run(TransactionManager::closeAndUnregister)
        val database = dbConfig.connect()
        TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
        transaction(database) {
            addLogger(Slf4jSqlDebugLogger)
            SchemaUtils.create(
                UserTable,
            )
        }
        database
    }

    val authDao: AuthDao = AuthDaoImpl(
        databaseFlow = databaseFlow
    )
    val authorizedApi: AuthorizedApi = AuthorizedApiImpl(
        authDao = authDao,
        scope = ioScope
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
}
