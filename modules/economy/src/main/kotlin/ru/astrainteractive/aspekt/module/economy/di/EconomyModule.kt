package ru.astrainteractive.aspekt.module.economy.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.command.di.EconomyCommandModule
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.aspekt.module.economy.integration.papi.di.PapiIntegrationModule
import ru.astrainteractive.aspekt.module.economy.service.di.EconomyServiceModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

interface EconomyModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : EconomyModule,
        Logger by JUtiltLogger("EconomyModule") {

        private val economyConfigModule = EconomyConfigModule.Default(
            coreModule = coreModule
        )

        private val databaseModule = EconomyDatabaseModule.Default(
            dbConfig = economyConfigModule.databaseConfiguration,
            dataFolder = economyConfigModule.folder,
            coroutineScope = coreModule.scope,
            ioDispatcher = coreModule.dispatchers.IO
        )

        private val commandModule = EconomyCommandModule.Default(
            coreModule = coreModule,
            databaseModule = databaseModule,
            bukkitCoreModule = bukkitCoreModule
        )

        private val papiIntegrationModule: PapiIntegrationModule = PapiIntegrationModule.Default(
            databaseModule = databaseModule,
            coreModule = coreModule
        )

        private val serviceModule = EconomyServiceModule.Default(
            economyConfigModule = economyConfigModule,
            databaseModule = databaseModule,
            bukkitCoreModule = bukkitCoreModule
        )

        private val lifecycles: List<Lifecycle>
            get() = listOf(
                economyConfigModule.lifecycle,
                databaseModule.lifecycle,
                commandModule.lifecycle,
                papiIntegrationModule.lifecycle,
                serviceModule.lifecycle
            )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = { lifecycles.forEach(Lifecycle::onEnable) },
            onReload = {
                lifecycles.forEach(Lifecycle::onReload)
                error { "#onReload - reload of economy module may break everything! Consider full server reload." }
            },
            onDisable = { lifecycles.forEach(Lifecycle::onDisable) }
        )
    }
}
