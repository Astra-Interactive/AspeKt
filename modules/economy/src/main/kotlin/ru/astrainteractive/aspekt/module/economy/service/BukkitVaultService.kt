package ru.astrainteractive.aspekt.module.economy.service

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.integration.vault.VaultEconomyProvider
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class BukkitVaultService(
    private val plugin: JavaPlugin,
    private val getCurrencies: () -> List<CurrencyModel>,
    private val repository: EconomyRepository
) : Logger by JUtiltLogger("BukkitService") {

    fun tryPrepare() {
        clear()

        val currencies = getCurrencies.invoke()
        if (currencies.isEmpty()) {
            error { "#prepare currency list is empty!" }
        }
        currencies.forEach { currency ->
            info { "#prepare Registered ${currency.id} currency as VaultEconomyProvider" }
            plugin.server.servicesManager.register(
                Economy::class.java,
                VaultEconomyProvider(
                    primaryCurrencyModel = currency,
                    repository = repository
                ),
                plugin,
                ServicePriority.entries.getOrElse(
                    index = currency.priority,
                    defaultValue = { ServicePriority.Normal }
                )
            )
        }
    }

    fun clear() {
        plugin.server.servicesManager.getRegistrations(plugin)
            .filter { it.service == Economy::class.java }
            .onEach { Bukkit.getServer().servicesManager.unregister(it.provider) }
    }
}
