package ru.astrainteractive.aspekt.module.rtp.api

import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.server.location.KLocation

interface SafeLocationProvider {
    suspend fun getLocation(ioScope: CoroutineScope, uuid: UUID): KLocation?

    fun hasTimeout(uuid: UUID): Boolean

    fun isActive(uuid: UUID): Boolean

    fun getJobsNumber(): Int

    fun getNextTickTime(): Double
}