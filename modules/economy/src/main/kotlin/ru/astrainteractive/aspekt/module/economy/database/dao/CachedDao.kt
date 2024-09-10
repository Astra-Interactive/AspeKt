package ru.astrainteractive.aspekt.module.economy.database.dao

import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel

internal interface CachedDao {
    fun getAllCurrencies(): List<CurrencyModel>
    fun reset()
}
