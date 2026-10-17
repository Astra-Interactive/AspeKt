package ru.astrainteractive.aspekt.module.economy.integration.papi.di.factory

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.integration.papi.EconomyPlaceholderExtension
import ru.astrainteractive.aspekt.module.economy.integration.papi.PlaceholderExpansionApi

internal class PapiFactory(
    private val economyRepository: EconomyRepository,
    private val scope: CoroutineScope
) {
    fun create(): PlaceholderExpansionApi {
        return EconomyPlaceholderExtension(
            repository = economyRepository,
            scope = scope
        )
    }
}
