package ru.astrainteractive.aspekt.module.economy.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.model.CurrencyConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.util.asStateFlowKrate
import ru.astrainteractive.klibs.kstorage.util.withDefault
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration

internal class EconomyConfigModule(coreModule: CoreModule) : Logger by JUtiltLogger("EconomyConfigModule") {
    val folder = coreModule.dataFolder.resolve("economy")

    val dbConfigKrate = coreModule.yamlFormat
        .krateOf<DatabaseConfiguration>(folder.resolve("db"))
        .withDefault { DatabaseConfiguration.H2(path = folder.resolve("db").path, arguments = emptyList()) }
        .asStateFlowKrate()

    val currencyConfigKrate = coreModule.yamlFormat
        .krateOf<CurrencyConfiguration>(file = folder.resolve("currencies"))
        .asStateFlowKrate()

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onReload = {
            dbConfigKrate.getValue()
            currencyConfigKrate.getValue()
        }
    )
}
