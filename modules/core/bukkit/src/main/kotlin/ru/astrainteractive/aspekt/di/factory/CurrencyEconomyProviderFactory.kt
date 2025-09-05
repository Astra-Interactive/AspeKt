package ru.astrainteractive.aspekt.di.factory

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astralibs.economy.EssentialsEconomyFacade
import ru.astrainteractive.astralibs.economy.VaultEconomyFacade
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

interface CurrencyEconomyProviderFactory {
    fun findByCurrencyId(currencyId: String): EconomyFacade?
    fun findDefault(): EconomyFacade?
}

internal class CurrencyEconomyProviderFactoryImpl :
    CurrencyEconomyProviderFactory,
    Logger by JUtiltLogger("CurrencyEconomyProviderFactory") {
    override fun findByCurrencyId(currencyId: String): EconomyFacade? {
        val registrations = Bukkit.getServer().servicesManager.getRegistrations(Economy::class.java)
        info { "#findEconomyProviderByCurrency registrations: ${registrations.size}" }
        val specificEconomyProvider = registrations
            .firstOrNull { it.provider.currencyNameSingular() == currencyId }
            ?.provider
            ?.let(::VaultEconomyFacade)
        if (specificEconomyProvider == null) {
            error { "#economyProvider could not find economy with currency: $currencyId" }
        }
        return specificEconomyProvider
    }

    override fun findDefault(): EconomyFacade? {
        return kotlin.runCatching {
            Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
                ?.provider
                ?.let(::VaultEconomyFacade)
        }.getOrNull() ?: kotlin.runCatching {
            info { "Could not find default vault economy provider" }
            if (!Bukkit.getServer().pluginManager.isPluginEnabled("Essentials")) {
                error("Essentials not enabled")
            } else {
                EssentialsEconomyFacade
            }
        }.getOrNull()
    }
}
