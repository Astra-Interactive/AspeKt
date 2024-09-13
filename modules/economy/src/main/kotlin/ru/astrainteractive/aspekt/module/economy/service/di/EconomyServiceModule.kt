package ru.astrainteractive.aspekt.module.economy.service.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.aspekt.module.economy.di.EconomyConfigModule
import ru.astrainteractive.aspekt.module.economy.service.BukkitVaultService
import ru.astrainteractive.aspekt.module.economy.service.PreHeatService
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal interface EconomyServiceModule {
    val lifecycle: Lifecycle

    class Default(
        private val economyConfigModule: EconomyConfigModule,
        coreModule: CoreModule,
        databaseModule: EconomyDatabaseModule
    ) : EconomyServiceModule {
        private val shouldSync get() = economyConfigModule.currencyConfiguration.cachedValue?.shouldSync == true

        private val bukkitVaultService = BukkitVaultService(
            plugin = coreModule.plugin.value,
            dao = databaseModule.economyDao,
            getCurrencies = {
                economyConfigModule.currencyConfiguration.cachedValue?.currencies
                    ?.values
                    .orEmpty()
                    .toList()
            },
        )

        private val preHeatService = PreHeatService(
            getCurrencies = {
                economyConfigModule.currencyConfiguration.cachedValue?.currencies
                    ?.values
                    .orEmpty()
                    .toList()
            },
            dao = databaseModule.economyDao
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                bukkitVaultService.tryPrepare()
                if (shouldSync) preHeatService.tryPreHeat()
            },
        )
    }
}
