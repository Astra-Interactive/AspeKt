package ru.astrainteractive.aspekt.module.chatgame.service

import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.astralibs.economy.EconomyFacade

internal class FakeCurrencyEconomyProviderFactory(
    private val defaultEconomy: EconomyFacade?,
    private val economiesByCurrencyId: Map<String, EconomyFacade>
) : CurrencyEconomyProviderFactory {
    override fun findByCurrencyId(currencyId: String): EconomyFacade? = economiesByCurrencyId[currencyId]

    override fun findDefault(): EconomyFacade? = defaultEconomy
}
