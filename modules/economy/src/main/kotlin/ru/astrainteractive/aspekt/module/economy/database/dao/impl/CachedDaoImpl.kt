package ru.astrainteractive.aspekt.module.economy.database.dao.impl

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.economy.database.dao.CachedDao
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.minutes

internal class CachedDaoImpl(
    private val economyDao: EconomyDao,
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineContext
) : CachedDao {

    private val allCurrenciesCache = Cache.Builder<Unit, List<CurrencyModel>>()
        .maximumCacheSize(1)
        .expireAfterWrite(1.minutes)
        .build()

    override fun getAllCurrencies(): List<CurrencyModel> {
        val cacheValue = allCurrenciesCache.get(Unit)
        if (cacheValue == null) {
            scope.launch(ioDispatcher) {
                allCurrenciesCache.put(Unit, economyDao.getAllCurrencies())
            }
        }
        return cacheValue.orEmpty()
    }

    override fun reset() {
        allCurrenciesCache.invalidateAll()
    }
}
