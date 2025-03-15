package ru.astrainteractive.aspekt.module.moneydrop.database.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

internal object MoneyDropLocationTable : LongIdTable(name = "MONEY_DROP_LOCATION") {
    val x = integer("x")
    val y = integer("y")
    val z = integer("z")
    val world = text("world")
    val additionalConstraint = text("additional_constraint").nullable()
    val instant = timestamp("timestamp")
}
