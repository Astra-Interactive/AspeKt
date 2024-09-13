package ru.astrainteractive.aspekt.module.economy.service

import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

internal class PreHeatService(
    private val getCurrencies: () -> List<CurrencyModel>,
    private val dao: EconomyDao
) : AsyncComponent(), Logger by JUtiltLogger("PreHeatService") {
    private var lastJob: Job? = null

    fun tryPreHeat() = launch {
        lastJob?.join()
        lastJob = coroutineContext.job
        dao.updateCurrencies(getCurrencies.invoke())
        lastJob = null
    }
}
