package ru.astrainteractive.aspekt.module.auth.api.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.StringFormat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.internal.AuthDaoImpl
import ru.astrainteractive.aspekt.module.auth.api.internal.AuthorizedApiImpl
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.api.table.UserTable
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration
import ru.astrainteractive.astralibs.exposed.model.connect
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrWriteIntoDefault
import ru.astrainteractive.astralibs.util.mapCached
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asCachedKrate
import java.io.File

class AuthApiModule(
    private val scope: CoroutineScope,
    private val dataFolder: File,
    private val stringFormat: StringFormat
) {
    private val databaseFlow: Flow<Database> = flowOf(DatabaseConfiguration.H2("auth_database"))
        .mapCached(scope) { dbConfig, previous ->
            previous?.connector?.invoke()?.close()
            previous?.run(TransactionManager::closeAndUnregister)
            val database = dbConfig.connect(dataFolder)
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
        scope = scope
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
