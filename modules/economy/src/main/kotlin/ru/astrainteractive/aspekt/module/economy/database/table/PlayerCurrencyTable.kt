package ru.astrainteractive.aspekt.module.economy.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object PlayerCurrencyTable : IntIdTable(name = "PLAYER_CURRENCY", columnName = "id") {
    val uuid = text("uuid")
    val currencyId = text("currency_id") // reference("currency_id", CurrencyTable.id)
    val lastUsername = text("last_username")
    val amount = double("amount")

    init {
        foreignKey(currencyId to CurrencyTable.id)
        uniqueIndex(uuid, currencyId)
    }
}
