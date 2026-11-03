@file:Suppress("ForbiddenPackageName", "KindShape")

package ru.astrainteractive.aspekt.module.economy.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.database.table.CurrencyTable
import ru.astrainteractive.aspekt.module.economy.database.table.PlayerCurrencyTable
import ru.astrainteractive.aspekt.module.economy.model.Currency
import ru.astrainteractive.aspekt.module.economy.model.EconomyPlayer
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

@Suppress("TooManyFunctions")
internal class ExposedEconomyRepository(
    private val databaseFlow: Flow<Database>
) : EconomyRepository,
    Logger by JUtiltLogger("EconomyRepository") {
    private val mutex = Mutex()

    private suspend fun <T> withMutex(block: suspend () -> T): T {
        return mutex.withLock { block.invoke() }
    }

    private suspend fun currentDatabase(): Database {
        return databaseFlow.first()
    }

    private fun toCurrency(row: ResultRow): Currency {
        return Currency(
            id = row[CurrencyTable.id].value,
            name = row[CurrencyTable.name],
            priority = row[CurrencyTable.priority],
        )
    }

    private fun toPlayerCurrency(row: ResultRow): PlayerCurrency {
        return PlayerCurrency(
            playerModel = EconomyPlayer(
                name = row[PlayerCurrencyTable.lastUsername],
                uuid = row[PlayerCurrencyTable.uuid],
            ),
            balance = row[PlayerCurrencyTable.amount],
            currencyModel = Currency(
                id = row[CurrencyTable.id].value,
                name = row[CurrencyTable.name],
                priority = row[CurrencyTable.priority],
            )
        )
    }

    override suspend fun loadAllCurrencies(): List<Currency> {
        return newSuspendedTransaction(db = currentDatabase()) {
            CurrencyTable.selectAll()
                .map(::toCurrency)
        }
    }

    override suspend fun updateCurrencies(currencies: List<Currency>) {
        return withMutex {
            val existingCurrencies = loadAllCurrencies()
            val nonExistingCurrencies = existingCurrencies
                .map(Currency::id)
                .toSet()
                .minus(currencies.map(Currency::id).toSet())
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
                    if (existingCurrencies.any { existingCurrency -> currency.id == existingCurrency.id }) {
                        CurrencyTable.update(where = { CurrencyTable.id eq currency.id }) { statement ->
                            statement[CurrencyTable.name] = currency.name
                            statement[CurrencyTable.priority] = currency.priority
                        }
                    } else {
                        CurrencyTable.insert { statement ->
                            statement[CurrencyTable.id] = currency.id
                            statement[CurrencyTable.name] = currency.name
                            statement[CurrencyTable.priority] = currency.priority
                        }
                    }
                }
            }
        }
    }

    override suspend fun findCurrency(id: String): Currency? {
        return newSuspendedTransaction(db = currentDatabase()) {
            CurrencyTable.selectAll()
                .where { CurrencyTable.id eq id }
                .map(::toCurrency)
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
                .map(::toPlayerCurrency)
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
                .map(::toPlayerCurrency)
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
                .orderBy(column = PlayerCurrencyTable.amount, order = SortOrder.DESC)
                .where { CurrencyTable.id eq id }
                .limit(size)
                .offset(page.times(size).toLong())
                .map(::toPlayerCurrency)
        }
    }

    private fun updatePlayerCurrencyWithoutTransaction(currency: PlayerCurrency) {
        val isPlayerCurrencyExists = PlayerCurrencyTable.selectAll()
            .where { PlayerCurrencyTable.currencyId eq currency.currencyModel.id }
            .andWhere { PlayerCurrencyTable.uuid eq currency.playerModel.uuid }
            .firstOrNull()
        if (isPlayerCurrencyExists == null) {
            PlayerCurrencyTable.insert { statement ->
                statement[PlayerCurrencyTable.amount] = currency.balance
                statement[PlayerCurrencyTable.lastUsername] = currency.playerModel.name
                statement[PlayerCurrencyTable.uuid] = currency.playerModel.uuid
                statement[PlayerCurrencyTable.currencyId] = currency.currencyModel.id
            }
        } else {
            PlayerCurrencyTable.update(
                where = {
                    PlayerCurrencyTable.currencyId.eq(currency.currencyModel.id).and {
                        PlayerCurrencyTable.uuid.eq(currency.playerModel.uuid)
                    }
                },
                body = { statement ->
                    statement[PlayerCurrencyTable.amount] = currency.balance
                    statement[PlayerCurrencyTable.lastUsername] = currency.playerModel.name
                }
            )
        }
    }

    override suspend fun transfer(
        from: EconomyPlayer,
        to: EconomyPlayer,
        amount: Double,
        currencyId: String
    ): Boolean {
        return withMutex {
            newSuspendedTransaction(db = currentDatabase()) {
                val fromPlayerCurrency =
                    findPlayerCurrency(from.uuid, currencyId) ?: return@newSuspendedTransaction false
                if (fromPlayerCurrency.balance - amount < 0) return@newSuspendedTransaction false
                val toPlayerCurrency = PlayerCurrency(
                    playerModel = EconomyPlayer(
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
    }

    override suspend fun updatePlayerCurrency(currency: PlayerCurrency) {
        return withMutex {
            newSuspendedTransaction(db = currentDatabase()) {
                updatePlayerCurrencyWithoutTransaction(currency)
            }
        }
    }
}
