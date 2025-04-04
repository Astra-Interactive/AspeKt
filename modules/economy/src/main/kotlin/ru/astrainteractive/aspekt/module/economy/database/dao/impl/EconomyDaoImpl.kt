package ru.astrainteractive.aspekt.module.economy.database.dao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.database.table.CurrencyTable
import ru.astrainteractive.aspekt.module.economy.database.table.PlayerCurrencyTable
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency
import ru.astrainteractive.aspekt.module.economy.model.PlayerModel
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

@Suppress("TooManyFunctions")
internal class EconomyDaoImpl(
    private val databaseFlow: Flow<Database>
) : EconomyDao,
    Logger by JUtiltLogger("EconomyDao") {
    private val mutex = Mutex()

    private suspend fun <T> withMutex(block: suspend () -> T): T {
        return mutex.withLock { block.invoke() }
    }

    private suspend fun currentDatabase(): Database {
        return databaseFlow.first()
    }

    private fun ResultRow.toCurrency() = CurrencyModel(
        id = this[CurrencyTable.id].value,
        name = this[CurrencyTable.name],
        priority = this[CurrencyTable.priority],
    )

    private fun ResultRow.toPlayerCurrency() = PlayerCurrency(
        playerModel = PlayerModel(
            name = this[PlayerCurrencyTable.lastUsername],
            uuid = this[PlayerCurrencyTable.uuid],
        ),
        balance = this[PlayerCurrencyTable.amount],
        currencyModel = CurrencyModel(
            id = this[CurrencyTable.id].value,
            name = this[CurrencyTable.name],
            priority = this[CurrencyTable.priority],
        )
    )

    override suspend fun getAllCurrencies(): List<CurrencyModel> {
        return newSuspendedTransaction(db = currentDatabase()) {
            CurrencyTable.selectAll()
                .map { it.toCurrency() }
        }
    }

    override suspend fun updateCurrencies(currencies: List<CurrencyModel>) = withMutex {
        val existingCurrencies = getAllCurrencies()
        val nonExistingCurrencies = existingCurrencies
            .map(CurrencyModel::id)
            .toSet()
            .minus(currencies.map(CurrencyModel::id).toSet())
        if (currencies.isEmpty()) {
            error { "#updateCurrencies you didn't setup any currencies! Economy may break!" }
        }
        if (nonExistingCurrencies.isNotEmpty()) {
            newSuspendedTransaction(db = currentDatabase()) {
                CurrencyTable.deleteWhere { CurrencyTable.id inList nonExistingCurrencies }
                PlayerCurrencyTable.deleteWhere { PlayerCurrencyTable.currencyId inList nonExistingCurrencies }
            }
        }
        newSuspendedTransaction(db = currentDatabase()) {
            currencies.forEach { currency ->
                if (existingCurrencies.find { currency.id == it.id } != null) {
                    CurrencyTable.update(where = { CurrencyTable.id eq currency.id }) {
                        it[CurrencyTable.name] = currency.name
                        it[CurrencyTable.priority] = currency.priority
                    }
                } else {
                    CurrencyTable.insert {
                        it[CurrencyTable.id] = currency.id
                        it[CurrencyTable.name] = currency.name
                        it[CurrencyTable.priority] = currency.priority
                    }
                }
            }
        }
    }

    override suspend fun findCurrency(id: String): CurrencyModel? {
        return newSuspendedTransaction(db = currentDatabase()) {
            CurrencyTable.selectAll()
                .where { CurrencyTable.id eq id }
                .map { it.toCurrency() }
                .firstOrNull()
        }
    }

    override suspend fun findPlayerCurrency(playerUuid: String, currencyId: String): PlayerCurrency? {
        return newSuspendedTransaction(db = currentDatabase()) {
            CurrencyTable.join(
                otherTable = PlayerCurrencyTable,
                joinType = JoinType.LEFT,
                onColumn = CurrencyTable.id,
                otherColumn = PlayerCurrencyTable.currencyId
            )
                .selectAll()
                .where { PlayerCurrencyTable.uuid eq playerUuid }
                .andWhere { PlayerCurrencyTable.currencyId eq currencyId }
                .map { it.toPlayerCurrency() }
                .firstOrNull()
        }
    }

    override suspend fun playerCurrencies(playerUuid: String): List<PlayerCurrency> {
        return newSuspendedTransaction(db = currentDatabase()) {
            CurrencyTable.join(
                otherTable = PlayerCurrencyTable,
                joinType = JoinType.LEFT,
                onColumn = CurrencyTable.id,
                otherColumn = PlayerCurrencyTable.currencyId
            )
                .selectAll()
                .where { PlayerCurrencyTable.uuid eq playerUuid }
                .map { it.toPlayerCurrency() }
        }
    }

    override suspend fun topCurrency(id: String, page: Int, size: Int): List<PlayerCurrency> {
        return newSuspendedTransaction(db = currentDatabase()) {
            CurrencyTable.join(
                otherTable = PlayerCurrencyTable,
                joinType = JoinType.INNER,
                onColumn = CurrencyTable.id,
                otherColumn = PlayerCurrencyTable.currencyId
            )
                .selectAll()
                .orderBy(PlayerCurrencyTable.amount to SortOrder.DESC)
                .where { CurrencyTable.id eq id }
                .limit(size)
                .offset((page * size).toLong())
                .map { it.toPlayerCurrency() }
        }
    }

    private suspend fun updatePlayerCurrencyWithoutTransaction(currency: PlayerCurrency) {
        val isPlayerCurrencyExists = PlayerCurrencyTable.selectAll()
            .where { PlayerCurrencyTable.currencyId eq currency.currencyModel.id }
            .andWhere { PlayerCurrencyTable.uuid eq currency.playerModel.uuid }
            .firstOrNull()
        if (isPlayerCurrencyExists == null) {
            PlayerCurrencyTable.insert {
                it[PlayerCurrencyTable.amount] = currency.balance
                it[PlayerCurrencyTable.lastUsername] = currency.playerModel.name
                it[PlayerCurrencyTable.uuid] = currency.playerModel.uuid
                it[PlayerCurrencyTable.currencyId] = currency.currencyModel.id
            }
        } else {
            PlayerCurrencyTable.update(
                where = {
                    PlayerCurrencyTable.currencyId.eq(currency.currencyModel.id).and {
                        PlayerCurrencyTable.uuid.eq(currency.playerModel.uuid)
                    }
                },
                body = {
                    it[PlayerCurrencyTable.amount] = currency.balance
                    it[PlayerCurrencyTable.lastUsername] = currency.playerModel.name
                }
            )
        }
    }

    override suspend fun transfer(
        from: PlayerModel,
        to: PlayerModel,
        amount: Double,
        currencyId: String
    ): Boolean = withMutex {
        newSuspendedTransaction(db = currentDatabase()) {
            val fromPlayerCurrency =
                findPlayerCurrency(from.uuid, currencyId) ?: return@newSuspendedTransaction false
            if (fromPlayerCurrency.balance - amount < 0) return@newSuspendedTransaction false
            val toPlayerCurrency = PlayerCurrency(
                playerModel = PlayerModel(
                    name = to.name,
                    uuid = to.uuid,
                ),
                balance = amount,
                currencyModel = fromPlayerCurrency.currencyModel
            )
            updatePlayerCurrencyWithoutTransaction(
                fromPlayerCurrency.copy(balance = fromPlayerCurrency.balance - amount)
            )
            updatePlayerCurrencyWithoutTransaction(toPlayerCurrency)
            true
        }
    }

    override suspend fun updatePlayerCurrency(currency: PlayerCurrency) = withMutex {
        newSuspendedTransaction(db = currentDatabase()) {
            updatePlayerCurrencyWithoutTransaction(currency)
        }
    }
}
