package ru.astrainteractive.aspekt.module.economy.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.model.CurrencyConfiguration
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.util.fileConfigKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultStateFlowMutableKrate
import java.io.File

internal interface EconomyConfigModule {
    val lifecycle: Lifecycle
    val folder: File
    val databaseConfiguration: DefaultStateFlowMutableKrate<DatabaseConfiguration>
    val currencyConfiguration: DefaultStateFlowMutableKrate<CurrencyConfiguration?>

    class Default(coreModule: CoreModule) : EconomyConfigModule, Logger by JUtiltLogger("EconomyConfigModule") {
        override val folder = coreModule.plugin.dataFolder.resolve("economy")

        override val databaseConfiguration = fileConfigKrate<DatabaseConfiguration>(
            file = folder.resolve("db"),
            stringFormat = coreModule.yamlFormat,
            factory = { DatabaseConfiguration.H2(name = "db", arguments = emptyList()) }
        )

        override val currencyConfiguration = fileConfigKrate<CurrencyConfiguration?>(
            file = folder.resolve("currencies"),
            stringFormat = coreModule.yamlFormat,
            factory = { null }
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onReload = {
                databaseConfiguration.loadAndGet()
                currencyConfiguration.loadAndGet()
            }
        )
    }
}
