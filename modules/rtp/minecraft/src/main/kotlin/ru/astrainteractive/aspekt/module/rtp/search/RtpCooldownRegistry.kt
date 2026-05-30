package ru.astrainteractive.aspekt.module.rtp.search

import com.google.common.cache.CacheBuilder
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

internal class RtpCooldownRegistry(cooldown: Duration = COOLDOWN) {

    private val timeout = CacheBuilder
        .newBuilder()
        .expireAfterWrite(cooldown.toJavaDuration())
        .build<UUID, Unit>()

    fun hasTimeout(uuid: UUID): Boolean {
        val hasTimeout = timeout.getIfPresent(uuid) != null
        if (!hasTimeout) timeout.put(uuid, Unit)
        return hasTimeout
    }

    private companion object {
        private val COOLDOWN = 10.seconds
    }
}
