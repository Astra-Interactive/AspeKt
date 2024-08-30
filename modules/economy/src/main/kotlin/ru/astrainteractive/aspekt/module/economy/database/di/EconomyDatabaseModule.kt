package ru.astrainteractive.aspekt.module.economy.database.di

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.aspekt.module.economy.database.table.CurrencyTable
import ru.astrainteractive.aspekt.module.economy.database.table.PlayerCurrencyTable
import ru.astrainteractive.aspekt.module.economy.model.DatabaseConfiguration
import java.io.File

interface EconomyDatabaseModule {

    class Default(dbConfig: DatabaseConfiguration, dataFolder: File) : EconomyDatabaseModule {
        private val database by lazy {
            val database = when (dbConfig) {
                DatabaseConfiguration.H2 -> Database.connect(
                    url = "jdbc:sqlite:${dataFolder.resolve("moneydrops.db").absolutePath}",
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
    }
}
