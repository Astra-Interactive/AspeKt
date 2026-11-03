package ru.astrainteractive.aspekt.module.economy.service

import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.model.Currency
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class PreHeatService(
    private val getCurrencies: () -> List<Currency>,
    dispatchers: KotlinDispatchers,
    private val repository: EconomyRepository
) : CoroutineFeature by CoroutineFeature.Default(dispatchers.IO).withTimings(),
    Logger by JUtiltLogger("PreHeatService") {
    private var lastJob: Job? = null

    fun tryPreHeat() {
        launch {
            lastJob?.join()
            lastJob = coroutineContext.job
            repository.updateCurrencies(getCurrencies.invoke())
            lastJob = null
        }
    }
}
