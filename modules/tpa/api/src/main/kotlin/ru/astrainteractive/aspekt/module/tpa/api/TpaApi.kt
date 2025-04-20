package ru.astrainteractive.aspekt.module.tpa.api

import com.google.common.cache.CacheBuilder
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import java.util.concurrent.TimeUnit

class TpaApi {
    enum class RequestType {
        TPA, TPAHERE
    }

    data class Request(
        val player: OnlineMinecraftPlayer,
        val type: RequestType
    )

    private val cache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build<OnlineMinecraftPlayer, Request>()

    fun tpa(executor: OnlineMinecraftPlayer, target: OnlineMinecraftPlayer) {
        cache.put(executor, Request(target, RequestType.TPA))
    }

    fun tpaHere(executor: OnlineMinecraftPlayer, target: OnlineMinecraftPlayer) {
        cache.put(executor, Request(target, RequestType.TPAHERE))
    }

    fun get(player: OnlineMinecraftPlayer): Map<OnlineMinecraftPlayer, Request> {
        return cache.asMap().filter { it.value.player == player }
    }

    fun cancel(player: OnlineMinecraftPlayer) {
        cache.invalidate(player)
    }

    fun deny(player: OnlineMinecraftPlayer): Set<OnlineMinecraftPlayer> {
        val players = cache.asMap()
            .filter { it.value.player == player }
            .keys
        players.forEach(::cancel)
        return players
    }

    /**
     * This player is awaiting to teleport
     */
    fun hasPendingRequest(player: OnlineMinecraftPlayer): Boolean {
        return cache.getIfPresent(player) != null
    }

    /**
     * Some people has request for this player
     */
    fun isBeingWaited(player: OnlineMinecraftPlayer): Boolean {
        return player in cache.asMap().values.map(Request::player)
    }
}
