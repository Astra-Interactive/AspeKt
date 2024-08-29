package ru.astrainteractive.aspekt.module.moneydrop.database.dao.impl

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
import kotlin.coroutines.CoroutineContext

internal class MoneyDropDaoImpl(
    private val database: Database,
    private val ioDispatcher: CoroutineContext
) : MoneyDropDao, Logger by JUtiltLogger("MoneyDropDao") {
    override suspend fun addLocation(location: MoneyDropLocation) {
        if (isLocationExists(location)) return
        runCatching {
            withContext(ioDispatcher) {
                transaction(database) {
                    MoneyDropLocationTable.insert {
                        it[MoneyDropLocationTable.x] = location.x
                        it[MoneyDropLocationTable.y] = location.y
                        it[MoneyDropLocationTable.z] = location.z
                        it[MoneyDropLocationTable.world] = location.world
                        it[MoneyDropLocationTable.additionalConstraint] = location.additionalConstraint
                    }
                }
            }
        }.onFailure { error { "#addLocation -> ${it.message}" } }
    }

    override suspend fun isLocationExists(location: MoneyDropLocation): Boolean {
        return runCatching {
            withContext(ioDispatcher) {
                transaction(database) {
                    MoneyDropLocationTable
                        .selectAll()
                        .where {
                            MoneyDropLocationTable.x.eq(location.x)
                                .and(MoneyDropLocationTable.y.eq(location.y))
                                .and(MoneyDropLocationTable.z.eq(location.z))
                                .and(MoneyDropLocationTable.world.eq(location.world))
                                .and(MoneyDropLocationTable.additionalConstraint.eq(location.additionalConstraint))
                        }.count() > 0
                }
            }
        }
            .onFailure { error { "#isLocationExists -> ${it.message}" } }
            .getOrElse { false }
    }
}
