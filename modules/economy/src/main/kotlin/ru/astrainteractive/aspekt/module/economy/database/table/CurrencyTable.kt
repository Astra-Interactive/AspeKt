package ru.astrainteractive.aspekt.module.economy.database.table

import ru.astrainteractive.klibs.mikro.exposed.dao.StringIdTable

object CurrencyTable : StringIdTable(name = "CURRENCY") {
    val name = text("name")
    val priority = integer("priority")
}
