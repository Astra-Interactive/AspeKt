package ru.astrainteractive.aspekt.module.tpa.api

import com.google.common.cache.CacheBuilder
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.concurrent.TimeUnit

class TpaApi {
    enum class RequestType {
        TPA, TPAHERE
    }

    data class Request(
        val player: OnlineKPlayer,
        val type: RequestType
    )

    private val cache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build<OnlineKPlayer, Request>()

    fun tpa(executor: OnlineKPlayer, target: OnlineKPlayer) {
        cache.put(executor, Request(target, RequestType.TPA))
    }

    fun tpaHere(executor: OnlineKPlayer, target: OnlineKPlayer) {
        cache.put(executor, Request(target, RequestType.TPAHERE))
    }

    fun get(player: OnlineKPlayer): Map<OnlineKPlayer, Request> {
        return cache.asMap().filter { it.value.player == player }
    }

    fun cancel(player: OnlineKPlayer) {
        cache.invalidate(player)
    }

    fun deny(player: OnlineKPlayer): Set<OnlineKPlayer> {
        val players = cache.asMap()
            .filter { it.value.player == player }
            .keys
        players.forEach(::cancel)
        return players
    }

    /**
     * This player is awaiting to teleport
     */
    fun hasPendingRequest(player: OnlineKPlayer): Boolean {
        return cache.getIfPresent(player) != null
    }

    /**
     * Some people has request for this player
     */
    fun isBeingWaited(player: OnlineKPlayer): Boolean {
        return player in cache.asMap().values.map(Request::player)
    }
}
