package ru.astrainteractive.aspekt.module.rtp.api.api

import kotlinx.coroutines.CoroutineScope
import java.util.UUID

interface SafeLocationProvider {
    suspend fun findLocation(ioScope: CoroutineScope, uuid: UUID): RtpSearchResult

    fun hasTimeout(uuid: UUID): Boolean

    fun isActive(uuid: UUID): Boolean

    fun countJobs(): Int

    fun resolveNextTickTime(): Double
}
