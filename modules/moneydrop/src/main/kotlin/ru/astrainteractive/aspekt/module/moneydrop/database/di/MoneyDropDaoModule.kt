package ru.astrainteractive.aspekt.module.moneydrop.database.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.aspekt.module.moneydrop.database.dao.MoneyDropDao
import ru.astrainteractive.aspekt.module.moneydrop.database.dao.impl.MoneyDropDaoImpl
import ru.astrainteractive.aspekt.module.moneydrop.database.table.MoneyDropLocationTable
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File
import kotlin.coroutines.CoroutineContext

internal interface MoneyDropDaoModule {
    val lifecycle: Lifecycle

    val dao: MoneyDropDao

    class Default(
        coroutineScope: CoroutineScope,
        dataFolder: File,
        ioDispatcher: CoroutineContext
    ) : MoneyDropDaoModule {
        private val database = flow {
            val path = dataFolder.resolve("moneydrops.db").absolutePath
            val database = Database.connect(
                url = "jdbc:sqlite:$path",
                driver = "org.sqlite.JDBC"
            )
            TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
            transaction(database) {
                addLogger(Slf4jSqlDebugLogger)
                SchemaUtils.create(
                    MoneyDropLocationTable,
                )
            }
            emit(database)
        }.shareIn(coroutineScope, SharingStarted.Eagerly, 1)

        override val dao: MoneyDropDao = MoneyDropDaoImpl(
            databaseFlow = database,
            ioDispatcher = ioDispatcher
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onDisable = {
                runBlocking { TransactionManager.closeAndUnregister(database.first()) }
            }
        )
    }
}
