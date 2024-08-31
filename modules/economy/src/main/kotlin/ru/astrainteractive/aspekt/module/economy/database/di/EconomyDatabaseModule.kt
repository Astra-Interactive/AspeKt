package ru.astrainteractive.aspekt.module.economy.database.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
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
import ru.astrainteractive.aspekt.module.economy.model.DatabaseConfiguration
import ru.astrainteractive.aspekt.util.FlowExt.mapHistory
import ru.astrainteractive.klibs.kstorage.api.flow.StateFlowKrate
import java.io.File
import kotlin.coroutines.CoroutineContext

internal interface EconomyDatabaseModule {
    val economyDao: EconomyDao
    val cachedDao: CachedDao

    class Default(
        dbConfig: StateFlowKrate<DatabaseConfiguration>,
        dataFolder: File,
        coroutineScope: CoroutineScope,
        ioDispatcher: CoroutineContext
    ) : EconomyDatabaseModule {
        private val databaseFlow: Flow<Database> = dbConfig.cachedStateFlow.mapHistory { dbConfig, previous ->
            previous?.connector?.invoke()?.close()

            val database = when (dbConfig) {
                is DatabaseConfiguration.H2 -> Database.connect(
                    url = "jdbc:sqlite:${dataFolder.resolve("${dbConfig.name}.db").absolutePath}",
                    driver = "org.sqlite.JDBC"
                )

                is DatabaseConfiguration.MySql -> Database.connect(
                    url = "jdbc:mysql://${dbConfig.host}:${dbConfig.port}/${dbConfig.name}",
                    driver = dbConfig.driver,
                    user = dbConfig.user,
                    password = dbConfig.password
                )
            }
            TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
            runBlocking {
                transaction(database) {
                    addLogger(Slf4jSqlDebugLogger)
                    SchemaUtils.create(
                        CurrencyTable,
                        PlayerCurrencyTable
                    )
                }
            }
            database
        }

        override val economyDao: EconomyDao = EconomyDaoImpl(databaseFlow)

        override val cachedDao: CachedDao = CachedDaoImpl(
            economyDao = economyDao,
            scope = coroutineScope,
            ioDispatcher = ioDispatcher
        )
    }
}
