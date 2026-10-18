package ru.astrainteractive.aspekt.module.economy.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.model.Currency
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class PreHeatService(
    private val getCurrencies: () -> List<Currency>,
    private val repository: EconomyRepository
) : CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO).withTimings(),
    Logger by JUtiltLogger("PreHeatService") {
    private var lastJob: Job? = null

    fun tryPreHeat() = launch {
        lastJob?.join()
        lastJob = coroutineContext.job
        repository.updateCurrencies(getCurrencies.invoke())
        lastJob = null
    }
}
