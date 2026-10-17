package ru.astrainteractive.aspekt.economy.api

import ru.astrainteractive.astralibs.economy.EconomyFacade

interface CurrencyEconomyProviderFactory {
    fun findByCurrencyId(currencyId: String): EconomyFacade?
    fun findDefault(): EconomyFacade?
}
