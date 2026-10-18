package ru.astrainteractive.aspekt.module.moneydrop.api

import ru.astrainteractive.aspekt.module.moneydrop.model.MoneyDropLocation

internal interface MoneyDropLocationRepository {
    suspend fun addLocation(location: MoneyDropLocation)
    suspend fun isLocationExists(location: MoneyDropLocation): Boolean
}
