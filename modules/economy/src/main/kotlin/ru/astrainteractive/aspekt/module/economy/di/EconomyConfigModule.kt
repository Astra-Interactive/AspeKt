package ru.astrainteractive.aspekt.module.economy.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.di.factory.ConfigKrateFactory
import ru.astrainteractive.aspekt.module.economy.model.CurrencyConfiguration
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.util.asStateFlowKrate
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration

internal class EconomyConfigModule(coreModule: CoreModule) : Logger by JUtiltLogger("EconomyConfigModule") {
    val folder = coreModule.dataFolder.resolve("economy")

    val databaseConfiguration = ConfigKrateFactory.fileConfigKrate<DatabaseConfiguration>(
        file = folder.resolve("db"),
        stringFormat = coreModule.yamlFormat,
        factory = { DatabaseConfiguration.H2(path = folder.resolve("db").path, arguments = emptyList()) }
    ).asStateFlowKrate()

    val currencyConfiguration = ConfigKrateFactory.fileConfigKrate<CurrencyConfiguration?>(
        file = folder.resolve("currencies"),
        stringFormat = coreModule.yamlFormat,
        factory = { null }
    ).asStateFlowKrate()

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onReload = {
            databaseConfiguration.getValue()
            currencyConfiguration.getValue()
        }
    )
}
