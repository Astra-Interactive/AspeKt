package ru.astrainteractive.aspekt.module.moneydrop.database.di

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
import java.io.File
import kotlin.coroutines.CoroutineContext

internal interface MoneyDropDaoModule {
    val dao: MoneyDropDao

    class Default(
        dataFolder: File,
        ioDispatcher: CoroutineContext
    ) : MoneyDropDaoModule {
        private val database by lazy {
            val path = dataFolder.resolve("moneydrops.db").absolutePath
            val database = Database.connect(
                url = "jdbc:sqlite:$path",
                driver = "org.sqlite.JDBC"
            )
            TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
            runBlocking {
                transaction(database) {
                    addLogger(Slf4jSqlDebugLogger)
                    SchemaUtils.create(
                        MoneyDropLocationTable,
                    )
                }
            }
            database
        }
        override val dao: MoneyDropDao = MoneyDropDaoImpl(
            database = database,
            ioDispatcher = ioDispatcher
        )
    }
}
