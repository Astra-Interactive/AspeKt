package ru.astrainteractive.aspekt.module.economy.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

internal object PlayerCurrencyTable : IntIdTable(name = "PLAYER_CURRENCY", columnName = "id") {
    val uuid = text("uuid")
    val currencyId = reference("currency_id", CurrencyTable)
    val lastUsername = text("last_username")
    val amount = double("amount")

    init {
        uniqueIndex(uuid, currencyId)
    }
}
