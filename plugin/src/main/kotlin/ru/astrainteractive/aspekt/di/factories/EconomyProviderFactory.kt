package ru.astrainteractive.aspekt.di.factories

import ru.astrainteractive.aspekt.util.EssentialsEconomyProvider
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.economy.VaultEconomyProvider
import ru.astrainteractive.klibs.kdi.Factory

class EconomyProviderFactory : Factory<EconomyProvider> {
    override fun create(): EconomyProvider {
        return runCatching { VaultEconomyProvider() }
            .onFailure { println("Could not get VaultEconomyProver: ${it.message}") }
            .getOrNull() ?: runCatching { EssentialsEconomyProvider() }
            .onFailure { it.printStackTrace() }
            .onSuccess { println("EssentialsEconomyProvider is active") }
            .getOrThrow()
    }
}
