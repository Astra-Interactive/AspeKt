package ru.astrainteractive.aspekt.module.tpa.api

import com.google.common.cache.CacheBuilder
import ru.astrainteractive.aspekt.module.tpa.model.TpaApiRequest
import ru.astrainteractive.aspekt.module.tpa.model.TpaApiRequestType
import java.util.UUID
import java.util.concurrent.TimeUnit

class TpaApi {

    @Suppress("MagicNumber")
    private val cache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build<UUID, TpaApiRequest>()

    fun tpa(executorUuid: UUID, targetUuid: UUID) {
        cache.put(executorUuid, TpaApiRequest(targetUuid, TpaApiRequestType.TPA))
    }

    fun tpaHere(executorUuid: UUID, targetUuid: UUID) {
        cache.put(executorUuid, TpaApiRequest(targetUuid, TpaApiRequestType.TPAHERE))
    }

    fun get(playerUuid: UUID): Map<UUID, TpaApiRequest> {
        return cache.asMap().filter { it.value.targetUuid == playerUuid }
    }

    fun cancel(playerUuid: UUID) {
        cache.invalidate(playerUuid)
    }

    fun deny(playerUuid: UUID): Set<UUID> {
        val requesters = cache.asMap()
            .filter { it.value.targetUuid == playerUuid }
            .keys
        requesters.forEach(::cancel)
        return requesters
    }

    /**
     * This player is awaiting to teleport
     */
    fun hasPendingRequest(playerUuid: UUID): Boolean {
        return cache.getIfPresent(playerUuid) != null
    }

    /**
     * Some people has request for this player
     */
    fun isBeingWaited(playerUuid: UUID): Boolean {
        return playerUuid in cache.asMap().values.map(TpaApiRequest::targetUuid)
    }
}
