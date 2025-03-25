package ru.astrainteractive.aspekt.module.moneydrop.database.dao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.aspekt.module.moneydrop.database.dao.MoneyDropDao
import ru.astrainteractive.aspekt.module.moneydrop.database.model.MoneyDropLocation
import ru.astrainteractive.aspekt.module.moneydrop.database.table.MoneyDropLocationTable
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import java.time.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

internal class MoneyDropDaoImpl(
    private val databaseFlow: Flow<Database>,
    private val ioDispatcher: CoroutineContext
) : MoneyDropDao, Logger by JUtiltLogger("MoneyDropDao") {
    override suspend fun addLocation(location: MoneyDropLocation) {
        if (isLocationExists(location)) return
        runCatching {
            withContext(ioDispatcher) {
                transaction(databaseFlow.first()) {
                    MoneyDropLocationTable.insert {
                        it[MoneyDropLocationTable.x] = location.x
                        it[MoneyDropLocationTable.y] = location.y
                        it[MoneyDropLocationTable.z] = location.z
                        it[MoneyDropLocationTable.world] = location.world
                        it[MoneyDropLocationTable.additionalConstraint] = location.additionalConstraint
                        it[MoneyDropLocationTable.instant] = location.instant
                    }
                }
            }
        }.onFailure { throwable -> error(throwable) { "#addLocation -> ${throwable.message}" } }
    }

    override suspend fun isLocationExists(location: MoneyDropLocation): Boolean {
        return runCatching {
            withContext(ioDispatcher) {
                transaction(databaseFlow.first()) {
                    MoneyDropLocationTable
                        .selectAll()
                        .where {
                            MoneyDropLocationTable.x.eq(location.x)
                                .and(MoneyDropLocationTable.y.eq(location.y))
                                .and(MoneyDropLocationTable.z.eq(location.z))
                                .and(MoneyDropLocationTable.world.eq(location.world))
                                .and(MoneyDropLocationTable.additionalConstraint.eq(location.additionalConstraint))
                                .and {
                                    val offset = Instant
                                        .now()
                                        .minus(MAX_MONEY_DROP_TIMEOUT)
                                    MoneyDropLocationTable
                                        .instant
                                        .greater(offset)
                                }
                        }.count() > 0
                }
            }
        }
            .onFailure { throwable -> error(throwable) { "#isLocationExists -> ${throwable.message}" } }
            .getOrElse { false }
    }

    operator fun Instant.minus(duration: Duration): Instant {
        return this.minusMillis(duration.inWholeMilliseconds)
    }

    companion object {
        private val MAX_MONEY_DROP_TIMEOUT = 1.days
    }
}
