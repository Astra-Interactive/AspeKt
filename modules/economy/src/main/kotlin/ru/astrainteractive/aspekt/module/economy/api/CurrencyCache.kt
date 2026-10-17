package ru.astrainteractive.aspekt.module.economy.api

import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel

internal interface CurrencyCache {
    fun getAllCurrencies(): List<CurrencyModel>
    fun reset()
}
