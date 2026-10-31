package ru.astrainteractive.aspekt.module.economy.service.di

import kotlinx.coroutines.cancel
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.core.di.BukkitCoreModule
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.aspekt.module.economy.di.EconomyConfigModule
import ru.astrainteractive.aspekt.module.economy.integration.vault.service.BukkitVaultService
import ru.astrainteractive.aspekt.module.economy.service.PreHeatService
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class EconomyServiceModule(
    bukkitCoreModule: BukkitCoreModule,
    databaseModule: EconomyDatabaseModule,
    dispatchers: KotlinDispatchers,
    private val economyConfigModule: EconomyConfigModule
) {
    private val shouldSync get() = economyConfigModule.currencyConfigKrate.cachedValue.shouldSync == true

    private val isVaultEnabled: Boolean
        get() = Bukkit.getPluginManager().isPluginEnabled("Vault")

    private val bukkitVaultService = BukkitVaultService(
        plugin = bukkitCoreModule.plugin,
        repository = databaseModule.economyRepository,
        dispatchers = dispatchers,
        getCurrencies = {
            economyConfigModule.currencyConfigKrate.cachedValue.currencies.values.toList()
        },
    )

    private val preHeatService = PreHeatService(
        getCurrencies = {
            economyConfigModule.currencyConfigKrate.cachedValue.currencies.values.toList()
        },
        dispatchers = dispatchers,
        repository = databaseModule.economyRepository
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            if (isVaultEnabled) {
                bukkitVaultService.tryPrepare()
            }
            if (shouldSync) preHeatService.tryPreHeat()
        },
        onDisable = {
            preHeatService.cancel()
            if (isVaultEnabled) {
                bukkitVaultService.clear()
            }
        }
    )
}
