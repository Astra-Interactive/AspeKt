package ru.astrainteractive.aspekt.module.moneydrop.database.dao

import ru.astrainteractive.aspekt.module.moneydrop.database.model.MoneyDropLocation

internal interface MoneyDropDao {
    suspend fun addLocation(location: MoneyDropLocation)
    suspend fun isLocationExists(location: MoneyDropLocation): Boolean
}
