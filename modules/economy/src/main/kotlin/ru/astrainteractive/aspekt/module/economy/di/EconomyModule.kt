package ru.astrainteractive.aspekt.module.economy.di

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.command.ekon.EkonCommandRegistry
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.aspekt.module.economy.integration.vault.VaultEconomyProvider
import ru.astrainteractive.aspekt.module.economy.model.CurrencyConfiguration
import ru.astrainteractive.aspekt.module.economy.model.DatabaseConfiguration
import ru.astrainteractive.aspekt.module.economy.service.PreHeatService
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultStateFlowMutableKrate

interface EconomyModule {
    val lifecycle: Lifecycle

    class Default(private val coreModule: CoreModule) : EconomyModule, Logger by JUtiltLogger("EconomyModule") {
        private val folder = coreModule.plugin.value.dataFolder.resolve("economy")
        private val databaseConfiguration = DefaultStateFlowMutableKrate(
            factory = { DatabaseConfiguration.H2() },
            loader = {
                folder.mkdirs()
                val file = folder.resolve("db.yml")
                if (!file.exists() || file.length() == 0L) {
                    file.createNewFile()
                    coreModule.yamlFormat.writeIntoFile(DatabaseConfiguration.H2(), file)
                }
                coreModule.yamlFormat.parse<DatabaseConfiguration>(file)
                    .onFailure { error { "#databaseConfiguration could not read db.yml: ${it.message}" } }
                    .getOrNull()
            }
        )
        private val currencyConfiguration = DefaultMutableKrate(
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
        private val databaseModule = EconomyDatabaseModule.Default(
            dbConfig = databaseConfiguration,
            dataFolder = folder
        )

        private fun reloadBukkitServiceManager() {
            Bukkit.getServer().servicesManager.getRegistrations(coreModule.plugin.value)
                .filter { it.service == Economy::class.java }
                .onEach { Bukkit.getServer().servicesManager.unregister(it.provider) }
            currencyConfiguration.cachedValue?.currencies.orEmpty().values.forEach { currency ->
                Bukkit.getServer().servicesManager.register(
                    Economy::class.java,
                    VaultEconomyProvider(
                        primaryCurrencyModel = currency,
                        dao = databaseModule.economyDao
                    ),
                    coreModule.plugin.value,
                    ServicePriority.Normal
                )
            }
        }

        private val econCommandRegistry = EkonCommandRegistry(
            plugin = coreModule.plugin.value,
            getCurrencies = { currencyConfiguration.cachedValue?.currencies?.values.orEmpty().toList() },
            getTranslation = { coreModule.translation.value },
            getKyori = { coreModule.kyoriComponentSerializer.value },
            dao = databaseModule.economyDao
        )

        private val preHeatService = PreHeatService(
            getCurrencies = { currencyConfiguration.cachedValue?.currencies?.values.orEmpty().toList() },
            dao = databaseModule.economyDao
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                reloadBukkitServiceManager()
                econCommandRegistry.register()
                if (currencyConfiguration.cachedValue?.shouldSync == true) {
                    preHeatService.preHeat()
                }
            },
            onReload = {
                reloadBukkitServiceManager()
                currencyConfiguration.loadAndGet()
                if (currencyConfiguration.cachedValue?.shouldSync == true) {
                    preHeatService.preHeat()
                }
            },
            onDisable = {
            }
        )
    }
}
