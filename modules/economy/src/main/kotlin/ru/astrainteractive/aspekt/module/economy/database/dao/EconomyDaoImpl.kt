package ru.astrainteractive.aspekt.module.economy.database.dao

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.astrainteractive.aspekt.module.economy.database.table.CurrencyTable
import ru.astrainteractive.aspekt.module.economy.database.table.PlayerCurrencyTable
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency
import ru.astrainteractive.aspekt.module.economy.model.PlayerModel

@Suppress("TooManyFunctions")
class EconomyDaoImpl(private val database: Database) : EconomyDao {
    private fun ResultRow.toCurrency() = CurrencyModel(
        id = this[CurrencyTable.id].value,
        name = this[CurrencyTable.name],
        isPrimary = this[CurrencyTable.isPrimary],
    )

    private fun ResultRow.toPlayerCurrency() = PlayerCurrency(
        name = this[PlayerCurrencyTable.lastUsername],
        uuid = this[PlayerCurrencyTable.id].value,
        amount = this[PlayerCurrencyTable.amount],
        currencyModel = CurrencyModel(
            id = this[CurrencyTable.id].value,
            name = this[CurrencyTable.name],
            isPrimary = this[CurrencyTable.isPrimary],
        )
    )

    override suspend fun getAllCurrencies(): List<CurrencyModel> {
        return transaction(database) {
            CurrencyTable.selectAll()
                .map { it.toCurrency() }
        }
    }

    override suspend fun updateCurrencies(currencies: List<CurrencyModel>) {
        val existingCurrencies = getAllCurrencies()
        val nonExistingCurrencies = existingCurrencies
            .map(CurrencyModel::id)
            .toSet()
            .minus(currencies.map(CurrencyModel::id).toSet())
        if (nonExistingCurrencies.isNotEmpty()) {
            transaction(database) {
                CurrencyTable.deleteWhere { CurrencyTable.id inList nonExistingCurrencies }
                PlayerCurrencyTable.deleteWhere { CurrencyTable.id inList nonExistingCurrencies }
            }
        }
        currencies.forEach { currency ->
            if (existingCurrencies.all { currency.id == it.id }) {
                CurrencyTable.update(where = { CurrencyTable.id eq currency.id }) {
                    it[CurrencyTable.name] = currency.name
                    it[CurrencyTable.isPrimary] = currency.isPrimary
                }
            } else {
                CurrencyTable.insert {
                    it[CurrencyTable.id] = currency.id
                    it[CurrencyTable.name] = currency.name
                    it[CurrencyTable.isPrimary] = currency.isPrimary
                }
            }
        }
    }

    override suspend fun findCurrency(id: String): CurrencyModel? {
        return transaction(database) {
            CurrencyTable.selectAll()
                .where { CurrencyTable.id eq id }
                .map { it.toCurrency() }
                .firstOrNull()
        }
    }

    override suspend fun findPlayerCurrency(playerUuid: String, currencyId: String): PlayerCurrency? {
        return transaction(database) {
            CurrencyTable.join(
                otherTable = PlayerCurrencyTable,
                joinType = JoinType.LEFT,
                onColumn = CurrencyTable.id,
                otherColumn = PlayerCurrencyTable.currencyId
            )
                .selectAll()
                .where { PlayerCurrencyTable.id eq playerUuid }
                .andWhere { PlayerCurrencyTable.currencyId eq currencyId }
                .map { it.toPlayerCurrency() }
                .firstOrNull()
        }
    }

    override suspend fun playerCurrencies(playerUuid: String): List<PlayerCurrency> {
        return transaction(database) {
            CurrencyTable.join(
                otherTable = PlayerCurrencyTable,
                joinType = JoinType.LEFT,
                onColumn = CurrencyTable.id,
                otherColumn = PlayerCurrencyTable.currencyId
            )
                .selectAll()
                .where { PlayerCurrencyTable.id eq playerUuid }
                .map { it.toPlayerCurrency() }
        }
    }

    override suspend fun topCurrency(id: String, page: Int, size: Int): List<PlayerCurrency> {
        return transaction(database) {
            CurrencyTable.join(
                otherTable = PlayerCurrencyTable,
                joinType = JoinType.LEFT,
                onColumn = CurrencyTable.id,
                otherColumn = PlayerCurrencyTable.currencyId
            )
                .selectAll()
                .orderBy(PlayerCurrencyTable.amount to SortOrder.DESC)
                .where { CurrencyTable.id eq id }
                .limit(n = size, offset = (page * size).toLong())
                .map { it.toPlayerCurrency() }
        }
    }

    private suspend fun updatePlayerCurrencyWithoutTransaction(currency: PlayerCurrency) {
        val isPlayerCurrencyExists = PlayerCurrencyTable.selectAll()
            .where { PlayerCurrencyTable.currencyId eq currency.currencyModel.id }
            .andWhere { PlayerCurrencyTable.id eq currency.uuid }
            .firstOrNull()
        if (isPlayerCurrencyExists == null) {
            PlayerCurrencyTable.insert {
                it[PlayerCurrencyTable.amount] = currency.amount
                it[PlayerCurrencyTable.lastUsername] = currency.name
                it[PlayerCurrencyTable.id] = currency.uuid
            }
        } else {
            PlayerCurrencyTable.update(where = { PlayerCurrencyTable.currencyId eq currency.currencyModel.id }) {
                it[PlayerCurrencyTable.amount] = currency.amount
                it[PlayerCurrencyTable.lastUsername] = currency.name
            }
        }
    }

    override suspend fun transfer(from: PlayerModel, to: PlayerModel, amount: Double, currencyId: String): Boolean {
        return newSuspendedTransaction(db = database) {
            val fromPlayerCurrency = findPlayerCurrency(from.uuid, currencyId) ?: return@newSuspendedTransaction false
            if (fromPlayerCurrency.amount - amount < 0) return@newSuspendedTransaction false
            val toPlayerCurrency = PlayerCurrency(
                name = to.name,
                amount = amount,
                uuid = to.uuid,
                currencyModel = fromPlayerCurrency.currencyModel
            )
            updatePlayerCurrency(fromPlayerCurrency.copy(amount = fromPlayerCurrency.amount - amount))
            updatePlayerCurrency(toPlayerCurrency)
            true
        }
    }

    override suspend fun updatePlayerCurrency(currency: PlayerCurrency) {
        return newSuspendedTransaction(db = database) {
            updatePlayerCurrencyWithoutTransaction(currency)
        }
    }
}
