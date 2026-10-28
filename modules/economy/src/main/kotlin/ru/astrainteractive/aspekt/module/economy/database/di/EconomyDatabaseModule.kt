@file:Suppress("ForbiddenPackageName")

package ru.astrainteractive.aspekt.module.economy.database.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import org.jetbrains.exposed.v1.core.Slf4jSqlDebugLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.astrainteractive.aspekt.module.economy.api.CurrencyCache
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.database.ExposedEconomyRepository
import ru.astrainteractive.aspekt.module.economy.database.table.CurrencyTable
import ru.astrainteractive.aspekt.module.economy.database.table.PlayerCurrencyTable
import ru.astrainteractive.aspekt.module.economy.memory.InMemoryCurrencyCache
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.StateFlowKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import ru.astrainteractive.klibs.mikro.exposed.util.connectAsFlow

internal class EconomyDatabaseModule(
    dbConfig: StateFlowKrate<DatabaseConfiguration>,
    ioScope: CoroutineScope,
    dispatchers: KotlinDispatchers
) : Logger by JUtiltLogger("EconomyDatabaseModule") {
    private val moduleIoScope = CoroutineScope(ioScope.coroutineContext + SupervisorJob())

    /**
     * Reconnects whenever [dbConfig] changes; the previous connection is closed and unregistered
     * by [connectAsFlow] when its flow is cancelled by [flatMapLatest].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val databaseFlow: Flow<Database> = dbConfig.cachedStateFlow
        .flatMapLatest(::connectDatabase)
        .onEach { database ->
            TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
            transaction(database) {
                addLogger(Slf4jSqlDebugLogger)
                SchemaUtils.create(
                    CurrencyTable,
                    PlayerCurrencyTable
                )
            }
        }
        .flowOn(dispatchers.IO)
        .shareIn(
            scope = moduleIoScope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    val economyRepository: EconomyRepository = ExposedEconomyRepository(databaseFlow)

    val currencyCache: CurrencyCache = InMemoryCurrencyCache(
        economyRepository = economyRepository,
        scope = moduleIoScope,
        ioDispatcher = dispatchers.IO
    )

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onReload = { currencyCache.reset() },
        onDisable = {
            currencyCache.reset()
            moduleIoScope.cancel()
        }
    )

    private fun connectDatabase(configuration: DatabaseConfiguration): Flow<Database> {
        return configuration.connectAsFlow()
    }
}
