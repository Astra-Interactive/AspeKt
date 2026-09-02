package ru.astrainteractive.aspekt.module.moneydrop.database.di

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
import ru.astrainteractive.aspekt.module.moneydrop.database.dao.MoneyDropDao
import ru.astrainteractive.aspekt.module.moneydrop.database.dao.impl.MoneyDropDaoImpl
import ru.astrainteractive.aspekt.module.moneydrop.database.table.MoneyDropLocationTable
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import ru.astrainteractive.klibs.mikro.exposed.util.connectAsFlow
import java.io.File
import kotlin.coroutines.CoroutineContext

class MoneyDropDaoModule(
    ioScope: CoroutineScope,
    dataFolder: File,
    ioDispatcher: CoroutineContext
) {
    private val moduleIoScope = CoroutineScope(ioScope.coroutineContext + SupervisorJob())

    private val databaseFlow: Flow<Database> = DatabaseConfiguration
        .SQLite(dataFolder.resolve("moneydrop_v2.db").absolutePath)
        .connectAsFlow()
        .onEach { database ->
            TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
            transaction(database) {
                addLogger(Slf4jSqlDebugLogger)
                SchemaUtils.create(
                    MoneyDropLocationTable,
                )
            }
        }
        .shareIn(moduleIoScope, SharingStarted.Lazily, replay = 1)

    internal val dao: MoneyDropDao = MoneyDropDaoImpl(
        databaseFlow = databaseFlow,
        ioDispatcher = ioDispatcher
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = { moduleIoScope.cancel() }
    )
}
