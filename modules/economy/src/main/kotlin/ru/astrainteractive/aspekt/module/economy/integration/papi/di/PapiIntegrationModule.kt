package ru.astrainteractive.aspekt.module.economy.integration.papi.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.aspekt.module.economy.integration.papi.api.PlaceholderExpansionApi
import ru.astrainteractive.aspekt.module.economy.integration.papi.di.factory.PapiFactory
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal class PapiIntegrationModule(
    databaseModule: EconomyDatabaseModule,
    coreModule: CoreModule,
) {

    private val isPapiEnabled: Boolean
        get() = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")

    private val economyPlaceholderExtension: PlaceholderExpansionApi by lazy {
        PapiFactory(
            economyRepository = databaseModule.economyRepository,
            scope = coreModule.ioScope
        ).create()
    }
    val lifecycle: Lifecycle = Lifecycle.Lambda(
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
