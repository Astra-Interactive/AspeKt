package ru.astrainteractive.aspekt.module.economy.database.dao

import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency
import ru.astrainteractive.aspekt.module.economy.model.PlayerModel

internal interface EconomyDao {
    /**
     * Fetch all available currencies from database
     */
    suspend fun getAllCurrencies(): List<CurrencyModel>

    /**
     * Insert or update currencies in database
     *
     * If currency is present in database and not present in [currencies] it will be deleted!
     */
    suspend fun updateCurrencies(currencies: List<CurrencyModel>)

    /**
     * Find currency by its id
     */
    suspend fun findCurrency(id: String): CurrencyModel?

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
    suspend fun transfer(from: PlayerModel, to: PlayerModel, amount: Double, currencyId: String): Boolean

    /**
     * Updates player currency amount
     * @return updated player currency
     */
    suspend fun updatePlayerCurrency(currency: PlayerCurrency)
}
