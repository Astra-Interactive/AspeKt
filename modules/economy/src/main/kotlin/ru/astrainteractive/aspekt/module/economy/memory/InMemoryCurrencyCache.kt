package ru.astrainteractive.aspekt.module.economy.memory

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.economy.api.CurrencyCache
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.minutes

internal class InMemoryCurrencyCache(
    private val economyRepository: EconomyRepository,
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineContext
) : CurrencyCache {

    private val allCurrenciesCache = Cache.Builder<Unit, List<CurrencyModel>>()
        .maximumCacheSize(1)
        .expireAfterWrite(1.minutes)
        .build()

    override fun getAllCurrencies(): List<CurrencyModel> {
        val cacheValue = allCurrenciesCache.get(Unit)
        if (cacheValue == null) {
            scope.launch(ioDispatcher) {
                allCurrenciesCache.put(Unit, economyRepository.getAllCurrencies())
            }
        }
        return cacheValue.orEmpty()
    }

    override fun reset() {
        allCurrenciesCache.invalidateAll()
    }
}
