package ru.astrainteractive.aspekt.module.rtp.search

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.astrainteractive.aspekt.module.rtp.api.RtpSearchResult
import java.util.UUID

/**
 * Tracks in-flight searches per player so a second request for the same player
 * reuses the running job instead of starting a duplicate.
 *
 * The [mutex] guards [jobMap] consistency; the job is awaited while holding it, so
 * concurrent searches are serialized — matching the single-job-at-a-time contract
 * the command layer enforces.
 */
internal class RtpSearchJobRegistry {

    private val mutex = Mutex()
    private val jobMap = HashMap<UUID, Deferred<RtpSearchResult>>()

    suspend fun await(
        uuid: UUID,
        ioScope: CoroutineScope,
        search: suspend () -> RtpSearchResult,
    ): RtpSearchResult = mutex.withLock {
        val deferred = jobMap.getOrPut(uuid) {
            ioScope.async { search() }
        }
        deferred.invokeOnCompletion {
            jobMap.remove(uuid)
        }
        deferred.await()
    }

    fun count(): Int = jobMap.size

    fun isActive(uuid: UUID): Boolean = jobMap[uuid]?.isActive == true
}
