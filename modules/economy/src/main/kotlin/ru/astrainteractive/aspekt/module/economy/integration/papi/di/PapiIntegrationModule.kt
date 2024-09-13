package ru.astrainteractive.aspekt.module.economy.integration.papi.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.aspekt.module.economy.integration.papi.PlaceholderExpansionApi
import ru.astrainteractive.aspekt.module.economy.integration.papi.di.factory.PapiFactory
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal interface PapiIntegrationModule {
    val lifecycle: Lifecycle

    class Default(
        databaseModule: EconomyDatabaseModule,
        coreModule: CoreModule,
    ) : PapiIntegrationModule {

        private val isPapiEnabled: Boolean
            get() = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")

        private val economyPlaceholderExtension: PlaceholderExpansionApi by lazy {
            PapiFactory(
                economyDao = databaseModule.economyDao,
                scope = coreModule.scope
            ).create()
        }
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                if (isPapiEnabled) {
                    economyPlaceholderExtension.register()
                }
            },
            onReload = {
                if (isPapiEnabled) {
                    if (economyPlaceholderExtension.isRegistered()) economyPlaceholderExtension.unregister()
                    economyPlaceholderExtension.register()
                }
            },
            onDisable = {
                if (isPapiEnabled) {
                    if (economyPlaceholderExtension.isRegistered()) economyPlaceholderExtension.unregister()
                }
            }
        )
    }
}
