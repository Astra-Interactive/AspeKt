package ru.astrainteractive.aspekt.module.jail.event

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.server.ServerCommandEvent
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.cache
import ru.astrainteractive.aspekt.module.jail.data.forget
import ru.astrainteractive.aspekt.module.jail.data.isInJail
import ru.astrainteractive.astralibs.event.EventListener

internal class JailEvent(
    private val cachedJailApi: CachedJailApi,
    private val jailController: JailController
) : EventListener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun serverCommandEvent(e: ServerCommandEvent) {
        val player = e.sender as? Player ?: return
        if (!cachedJailApi.isInJail(player)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerCommandPreprocessEvent(e: PlayerCommandPreprocessEvent) {
        if (!cachedJailApi.isInJail(e.player)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onJoin(e: PlayerJoinEvent) {
        cachedJailApi.cache(e.player)
        if (!cachedJailApi.isInJail(e.player)) return
        jailController.tryTeleportToJail(e.player.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onQuit(e: PlayerQuitEvent) {
        cachedJailApi.forget(e.player)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onRespawn(e: PlayerRespawnEvent) {
        cachedJailApi.cache(e.player)
        if (!cachedJailApi.isInJail(e.player)) return
        jailController.tryTeleportToJail(e.player.uniqueId)
    }
}
