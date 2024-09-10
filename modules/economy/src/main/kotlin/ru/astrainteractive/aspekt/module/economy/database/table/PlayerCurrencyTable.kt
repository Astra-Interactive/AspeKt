package ru.astrainteractive.aspekt.module.economy.database.table

import ru.astrainteractive.aspekt.module.economy.database.core.StringIdTable

internal object PlayerCurrencyTable : StringIdTable(name = "PLAYER_CURRENCY", columnName = "uuid") {
    val currencyId = reference("currency_id", CurrencyTable)
    val lastUsername = text("last_username")
    val amount = double("amount")
}
