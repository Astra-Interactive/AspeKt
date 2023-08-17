package ru.astrainteractive.aspekt.di.factories

import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.economy.VaultEconomyProvider
import ru.astrainteractive.klibs.kdi.Factory

class EconomyProviderFactory : Factory<EconomyProvider> {
    override fun create(): EconomyProvider {
        return VaultEconomyProvider()
    }
}
