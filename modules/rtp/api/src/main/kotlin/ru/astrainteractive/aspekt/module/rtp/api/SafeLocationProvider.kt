package ru.astrainteractive.aspekt.module.rtp.api

import kotlinx.coroutines.CoroutineScope
import java.util.UUID

interface SafeLocationProvider {
    suspend fun getLocation(ioScope: CoroutineScope, uuid: UUID): RtpSearchResult

    fun hasTimeout(uuid: UUID): Boolean

    fun isActive(uuid: UUID): Boolean

    fun getJobsNumber(): Int

    fun getNextTickTime(): Double
}
