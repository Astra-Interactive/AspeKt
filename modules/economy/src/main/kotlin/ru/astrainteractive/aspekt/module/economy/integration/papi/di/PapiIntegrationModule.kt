package ru.astrainteractive.aspekt.module.economy.integration.papi.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.aspekt.module.economy.integration.papi.EconomyPlaceholderExtension
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal interface PapiIntegrationModule {
    val lifecycle: Lifecycle

    class Default(
        databaseModule: EconomyDatabaseModule,
        coreModule: CoreModule,
    ) : PapiIntegrationModule {

        private val isPapiExists: Boolean
            get() = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null

        private val economyPlaceholderExtension by lazy {
            EconomyPlaceholderExtension(
                dao = databaseModule.economyDao,
                scope = coreModule.scope
            )
        }
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                if (isPapiExists) {
                    economyPlaceholderExtension.register()
                }
            },
            onReload = {
                if (isPapiExists) {
                    if (economyPlaceholderExtension.isRegistered) economyPlaceholderExtension.unregister()
                    economyPlaceholderExtension.register()
                }
            },
            onDisable = {
                if (isPapiExists) {
                    if (economyPlaceholderExtension.isRegistered) economyPlaceholderExtension.unregister()
                }
            }
        )
    }
}
