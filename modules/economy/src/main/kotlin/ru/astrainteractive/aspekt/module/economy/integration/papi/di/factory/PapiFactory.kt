package ru.astrainteractive.aspekt.module.economy.integration.papi.di.factory

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.integration.papi.EconomyPlaceholderExtension
import ru.astrainteractive.aspekt.module.economy.integration.papi.PlaceholderExpansionApi

internal class PapiFactory(
    private val economyDao: EconomyDao,
    private val scope: CoroutineScope
) {
    fun create(): PlaceholderExpansionApi {
        return EconomyPlaceholderExtension(
            dao = economyDao,
            scope = scope
        )
    }
}
