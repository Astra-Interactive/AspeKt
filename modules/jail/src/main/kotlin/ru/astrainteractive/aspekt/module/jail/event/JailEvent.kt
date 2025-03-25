package ru.astrainteractive.aspekt.module.jail.event

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.cache
import ru.astrainteractive.aspekt.module.jail.data.forget
import ru.astrainteractive.aspekt.module.jail.data.isInJail
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kstorage.api.Krate

internal class JailEvent(
    private val cachedJailApi: CachedJailApi,
    private val jailController: JailController,
    kyoriKrate: Krate<KyoriComponentSerializer>,
    translationKrate: Krate<PluginTranslation>
) : EventListener, Logger by JUtiltLogger("AspeKt-JailEvent") {
    private val kyori by kyoriKrate
    private val translation by translationKrate

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerCommandPreprocessEvent(e: PlayerCommandPreprocessEvent) {
        if (!cachedJailApi.isInJail(e.player)) return
        e.isCancelled = true
        with(kyori) { e.player.sendMessage(translation.jails.jailedCommandBlocked.component) }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onJoin(e: PlayerJoinEvent) {
        cachedJailApi.cache(e.player)
        if (!cachedJailApi.isInJail(e.player)) return
        jailController.tryTeleportToJail(e.player.uniqueId)
        with(kyori) { e.player.sendMessage(translation.jails.youInJail.component) }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onTeleport(e: PlayerTeleportEvent) {
        cachedJailApi.cache(e.player)
        if (!cachedJailApi.isInJail(e.player)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInteract(e: PlayerInteractEvent) {
        cachedJailApi.cache(e.player)
        if (!cachedJailApi.isInJail(e.player)) return
        e.isCancelled = true
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
        with(kyori) { e.player.sendMessage(translation.jails.youInJail.component) }
    }
}
