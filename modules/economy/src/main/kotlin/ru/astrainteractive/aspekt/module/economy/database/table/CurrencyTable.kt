package ru.astrainteractive.aspekt.module.economy.database.table

import ru.astrainteractive.astralibs.exposed.table.StringIdTable

internal object CurrencyTable : StringIdTable(name = "CURRENCY") {
    val name = text("name")
    val priority = integer("priority")
}
