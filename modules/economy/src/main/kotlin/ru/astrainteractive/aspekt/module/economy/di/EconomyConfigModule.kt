package ru.astrainteractive.aspekt.module.economy.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.model.CurrencyConfiguration
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultStateFlowMutableKrate
import java.io.File

internal interface EconomyConfigModule {
    val lifecycle: Lifecycle
    val folder: File
    val databaseConfiguration: DefaultStateFlowMutableKrate<DatabaseConfiguration>
    val currencyConfiguration: DefaultMutableKrate<CurrencyConfiguration?>

    class Default(coreModule: CoreModule) : EconomyConfigModule, Logger by JUtiltLogger("EconomyConfigModule") {
        override val folder = coreModule.plugin.dataFolder.resolve("economy")

        override val databaseConfiguration = DefaultStateFlowMutableKrate(
            factory = { DatabaseConfiguration.H2(name = "db", arguments = emptyList()) },
            loader = {
                folder.mkdirs()
                val file = folder.resolve("db.yml")
                if (!file.exists() || file.length() == 0L) {
                    file.createNewFile()
                    coreModule.yamlFormat.writeIntoFile<DatabaseConfiguration>(
                        value = DatabaseConfiguration.H2(
                            name = "db",
                            arguments = emptyList()
                        ),
                        file = file
                    )
                }
                coreModule.yamlFormat.parse<DatabaseConfiguration>(file)
                    .onFailure { error { "#databaseConfiguration could not read db.yml: ${it.message}" } }
                    .getOrNull()
            }
        )

        override val currencyConfiguration = DefaultMutableKrate(
            factory = { null },
            loader = {
                folder.mkdirs()
                val file = folder.resolve("currencies.yml")
                if (!file.exists() || file.length() == 0L) {
                    file.createNewFile()
                    coreModule.yamlFormat.writeIntoFile(CurrencyConfiguration(), file)
                }
                coreModule.yamlFormat
                    .parse<CurrencyConfiguration>(file)
                    .onFailure { error { "#currencyConfiguration could not read currencies.yml: ${it.message}" } }
                    .getOrNull()
            }
        )
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onReload = {
                databaseConfiguration.loadAndGet()
                currencyConfiguration.loadAndGet()
            }
        )
    }
}
