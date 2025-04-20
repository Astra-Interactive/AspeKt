package ru.astrainteractive.aspekt.module.rtp.command

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.minecraft.location.Location
import java.util.UUID

interface SafeLocationProvider {
    suspend fun getLocation(scope: CoroutineScope, uuid: UUID): Location?

    fun hasTimeout(uuid: UUID): Boolean

    fun isActive(uuid: UUID): Boolean

    fun getJobsNumber(): Int
}
