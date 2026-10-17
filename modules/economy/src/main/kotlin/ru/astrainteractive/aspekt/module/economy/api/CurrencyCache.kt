package ru.astrainteractive.aspekt.module.economy.api

import ru.astrainteractive.aspekt.module.economy.model.Currency

internal interface CurrencyCache {
    fun getAllCurrencies(): List<Currency>
    fun reset()
}
