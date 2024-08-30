package ru.astrainteractive.aspekt.module.economy.database.table

import ru.astrainteractive.aspekt.module.economy.database.core.StringIdTable

object CurrencyTable : StringIdTable(name = "CURRENCY") {
    val name = text("name")
    val isPrimary = bool("is_primary")
}
