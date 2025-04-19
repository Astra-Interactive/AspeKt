package ru.astrainteractive.aspekt.module.tpa.api

import com.google.common.cache.CacheBuilder
import java.util.UUID
import java.util.concurrent.TimeUnit

class TpaApi {
    enum class RequestType {
        TPA, TPAHERE
    }

    data class Request(
        val uuid: UUID,
        val type: RequestType
    )

    private val cache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build<UUID, Request>()

    fun tpa(executor: UUID, target: UUID) {
        cache.put(executor, Request(target, RequestType.TPA))
    }

    fun tpaHere(executor: UUID, target: UUID) {
        cache.put(executor, Request(target, RequestType.TPAHERE))
    }

    fun get(uuid: UUID): Map<UUID, Request> {
        return cache.asMap().filter { it.value.uuid == uuid }
    }

    fun cancel(uuid: UUID) {
        cache.invalidate(uuid)
    }

    fun deny(uuid: UUID): Set<UUID> {
        val uuids = cache.asMap()
            .filter { it.value.uuid == uuid }
            .keys
        uuids.forEach(::cancel)
        return uuids
    }

    /**
     * This player is awaiting to teleport
     */
    fun hasPendingRequest(uuid: UUID): Boolean {
        return cache.getIfPresent(uuid) != null
    }

    /**
     * Some people has request for this player
     */
    fun isBeingWaited(uuid: UUID): Boolean {
        return uuid in cache.asMap().values.map(Request::uuid)
    }
}
