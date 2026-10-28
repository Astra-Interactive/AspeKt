package ru.astrainteractive.aspekt.module.economy.api

import ru.astrainteractive.aspekt.module.economy.model.Currency

internal interface CurrencyCache {
    fun loadAllCurrencies(): List<Currency>
    fun reset()
}
