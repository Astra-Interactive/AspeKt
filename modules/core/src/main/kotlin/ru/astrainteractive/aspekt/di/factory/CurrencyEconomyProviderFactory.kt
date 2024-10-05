package ru.astrainteractive.aspekt.di.factory

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.economy.EssentialsEconomyProvider
import ru.astrainteractive.astralibs.economy.VaultEconomyProvider
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

interface CurrencyEconomyProviderFactory {
    fun findByCurrencyId(currencyId: String): EconomyProvider?
    fun findDefault(): EconomyProvider?
}

internal class CurrencyEconomyProviderFactoryImpl(
    private val plugin: JavaPlugin,
) : CurrencyEconomyProviderFactory,
    Logger by JUtiltLogger("CurrencyEconomyProviderFactory") {
    override fun findByCurrencyId(currencyId: String): EconomyProvider? {
        val registrations = Bukkit.getServer().servicesManager.getRegistrations(Economy::class.java)
        info { "#findEconomyProviderByCurrency registrations: ${registrations.size}" }
        val specificEconomyProvider = registrations
            .firstOrNull { it.provider.currencyNameSingular() == currencyId }
            ?.provider
            ?.let(::VaultEconomyProvider)
        if (specificEconomyProvider == null) {
            error { "#economyProvider could not find economy with currency: $currencyId" }
        }
        return specificEconomyProvider
    }

    override fun findDefault(): EconomyProvider? {
        return kotlin.runCatching {
            Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
                ?.provider
                ?.let(::VaultEconomyProvider)
        }.getOrNull() ?: kotlin.runCatching {
            info { "Could not find default vault economy provider" }
            if (!Bukkit.getServer().pluginManager.isPluginEnabled("Essentials")) {
                error("Essentials not enabled")
            } else {
                EssentialsEconomyProvider
            }
        }.getOrNull()
    }
}
