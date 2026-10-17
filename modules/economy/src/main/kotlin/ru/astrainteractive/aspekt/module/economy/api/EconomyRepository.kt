package ru.astrainteractive.aspekt.module.economy.api

import ru.astrainteractive.aspekt.module.economy.model.Currency
import ru.astrainteractive.aspekt.module.economy.model.EconomyPlayer
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency

internal interface EconomyRepository {
    /**
     * Fetch all available currencies from database
     */
    suspend fun getAllCurrencies(): List<Currency>

    /**
     * Insert or update currencies in database
     *
     * If currency is present in database and not present in [currencies] it will be deleted!
     */
    suspend fun updateCurrencies(currencies: List<Currency>)

    /**
     * Find currency by its id
     */
    suspend fun findCurrency(id: String): Currency?

    /**
     * Find player currency amount
     * @return amount of [currencyId]
     */
    suspend fun findPlayerCurrency(playerUuid: String, currencyId: String): PlayerCurrency?

    /**
     * Find all existing currencies for player
     * @return amount of all currencies for player
     */
    suspend fun playerCurrencies(playerUuid: String): List<PlayerCurrency>

    /**
     * List top players by currency
     * @return list of top players by currency
     */
    suspend fun topCurrency(id: String, page: Int, size: Int): List<PlayerCurrency>

    /**
     * Transfer money in transaction from player [from] to player [to]
     * @return true if success false if not
     */
    suspend fun transfer(from: EconomyPlayer, to: EconomyPlayer, amount: Double, currencyId: String): Boolean

    /**
     * Updates player currency amount
     * @return updated player currency
     */
    suspend fun updatePlayerCurrency(currency: PlayerCurrency)
}
