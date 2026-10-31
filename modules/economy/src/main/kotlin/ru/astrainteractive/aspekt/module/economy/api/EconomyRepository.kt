package ru.astrainteractive.aspekt.module.economy.api

import ru.astrainteractive.aspekt.module.economy.model.Currency
import ru.astrainteractive.aspekt.module.economy.model.EconomyPlayer
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency

internal interface EconomyRepository {
    suspend fun loadAllCurrencies(): List<Currency>

    suspend fun updateCurrencies(currencies: List<Currency>)

    suspend fun findCurrency(id: String): Currency?

    suspend fun findPlayerCurrency(playerUuid: String, currencyId: String): PlayerCurrency?

    suspend fun playerCurrencies(playerUuid: String): List<PlayerCurrency>

    suspend fun topCurrency(id: String, page: Int, size: Int): List<PlayerCurrency>

    suspend fun transfer(from: EconomyPlayer, to: EconomyPlayer, amount: Double, currencyId: String): Boolean

    suspend fun updatePlayerCurrency(currency: PlayerCurrency)
}
