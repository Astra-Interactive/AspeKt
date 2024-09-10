package ru.astrainteractive.aspekt.module.economy.service

import kotlinx.coroutines.launch
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.integration.vault.VaultEconomyProvider
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

internal class BukkitVaultService(
    private val plugin: JavaPlugin,
    private val dao: EconomyDao
) : AsyncComponent(), Logger by JUtiltLogger("BukkitService") {

    fun prepare() = launch {
        Bukkit.getServer().servicesManager.getRegistrations(plugin)
            .filter { it.service == Economy::class.java }
            .onEach { Bukkit.getServer().servicesManager.unregister(it.provider) }
        val currencies = dao.getAllCurrencies()
        if (currencies.isEmpty()) {
            error { "#prepare currency list is empty!" }
        }
        dao.getAllCurrencies().forEach { currency ->
            Bukkit.getServer().servicesManager.register(
                Economy::class.java,
                VaultEconomyProvider(
                    primaryCurrencyModel = currency,
                    dao = dao
                ),
                plugin,
                ServicePriority.Normal
            )
        }
    }
}
