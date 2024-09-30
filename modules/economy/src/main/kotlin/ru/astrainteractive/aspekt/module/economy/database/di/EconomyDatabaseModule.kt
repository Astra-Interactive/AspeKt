package ru.astrainteractive.aspekt.module.economy.database.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.aspekt.module.economy.database.dao.CachedDao
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.database.dao.impl.CachedDaoImpl
import ru.astrainteractive.aspekt.module.economy.database.dao.impl.EconomyDaoImpl
import ru.astrainteractive.aspekt.module.economy.database.table.CurrencyTable
import ru.astrainteractive.aspekt.module.economy.database.table.PlayerCurrencyTable
import ru.astrainteractive.astralibs.exposed.factory.DatabaseFactory
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.util.FlowExt.mapCached
import ru.astrainteractive.klibs.kstorage.api.flow.StateFlowKrate
import java.io.File
import kotlin.coroutines.CoroutineContext

internal interface EconomyDatabaseModule {
    val lifecycle: Lifecycle
    val databaseFlow: Flow<Database>
    val economyDao: EconomyDao
    val cachedDao: CachedDao

    class Default(
        dbConfig: StateFlowKrate<DatabaseConfiguration>,
        dataFolder: File,
        coroutineScope: CoroutineScope,
        ioDispatcher: CoroutineContext
    ) : EconomyDatabaseModule, Logger by JUtiltLogger("EconomyDatabaseModule") {

        override val databaseFlow: Flow<Database> = dbConfig.cachedStateFlow
            .mapCached<DatabaseConfiguration, Database> { dbConfig, previous ->
                previous?.connector?.invoke()?.close()
                previous?.run(TransactionManager::closeAndUnregister)
                val database = DatabaseFactory(dataFolder).create(dbConfig)
                TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
                transaction(database) {
                    addLogger(Slf4jSqlDebugLogger)
                    SchemaUtils.create(
                        CurrencyTable,
                        PlayerCurrencyTable
                    )
                }
                database
            }.shareIn(coroutineScope, SharingStarted.Eagerly, 1)

        override val economyDao: EconomyDao = EconomyDaoImpl(databaseFlow)

        override val cachedDao: CachedDao = CachedDaoImpl(
            economyDao = economyDao,
            scope = coroutineScope,
            ioDispatcher = ioDispatcher
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onReload = { cachedDao.reset() }
        )
    }
}