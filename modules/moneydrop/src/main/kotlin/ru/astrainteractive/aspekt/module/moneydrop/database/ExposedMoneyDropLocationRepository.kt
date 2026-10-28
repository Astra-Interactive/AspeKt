@file:Suppress("ForbiddenPackageName", "KindShape")

package ru.astrainteractive.aspekt.module.moneydrop.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.astrainteractive.aspekt.module.moneydrop.api.MoneyDropLocationRepository
import ru.astrainteractive.aspekt.module.moneydrop.database.table.MoneyDropLocationTable
import ru.astrainteractive.aspekt.module.moneydrop.model.MoneyDropLocation
import ru.astrainteractive.klibs.mikro.core.coroutines.propagateCancellationException
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

internal class ExposedMoneyDropLocationRepository(
    private val databaseFlow: Flow<Database>,
    private val ioDispatcher: CoroutineContext
) : MoneyDropLocationRepository, Logger by JUtiltLogger("MoneyDropLocationRepository") {

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
                                    val offset = Clock.System.now()
                                        .minus(MAX_MONEY_DROP_TIMEOUT)
                                        .toEpochMilliseconds()
                                        .let(java.time.Instant::ofEpochMilli)
                                    MoneyDropLocationTable
                                        .instant
                                        .greater(offset)
                                }
                        }.count() > 0
                }
            }
        }
            .propagateCancellationException()
            .onFailure { throwable -> error(throwable) { "#isLocationExists -> ${throwable.message}" } }
            .getOrElse { false }
    }

    override suspend fun addLocation(location: MoneyDropLocation) {
        if (isLocationExists(location)) return
        runCatching {
            withContext(ioDispatcher) {
                transaction(databaseFlow.first()) {
                    MoneyDropLocationTable.insert { statement ->
                        statement[MoneyDropLocationTable.x] = location.x
                        statement[MoneyDropLocationTable.y] = location.y
                        statement[MoneyDropLocationTable.z] = location.z
                        statement[MoneyDropLocationTable.world] = location.world
                        statement[MoneyDropLocationTable.additionalConstraint] = location.additionalConstraint
                        statement[MoneyDropLocationTable.instant] = java.time.Instant.ofEpochMilli(
                            location.instant.toEpochMilliseconds()
                        )
                    }
                }
            }
        }.propagateCancellationException().onFailure { throwable ->
            error(throwable) { "#addLocation -> ${throwable.message}" }
        }
    }

    companion object {
        private val MAX_MONEY_DROP_TIMEOUT = 30.days
    }
}
