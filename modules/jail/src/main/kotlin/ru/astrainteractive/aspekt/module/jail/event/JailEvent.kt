package ru.astrainteractive.aspekt.module.jail.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.server.ServerLoadEvent
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.module.jail.data.cache
import ru.astrainteractive.aspekt.module.jail.data.forget
import ru.astrainteractive.aspekt.module.jail.data.isInJail
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class JailEvent(
    private val jailApi: JailApi,
    private val cachedJailApi: CachedJailApi,
    private val jailController: JailController,
    private val scope: CoroutineScope,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<PluginTranslation>
) : EventListener,
    Logger by JUtiltLogger("AspeKt-JailEvent"),
    KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerCommandPreprocessEvent(e: PlayerCommandPreprocessEvent) {
        if (!cachedJailApi.isInJail(e.player)) return
        e.isCancelled = true
        e.player.sendMessage(translation.jails.jailedCommandBlocked.component)
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

    private fun teleportBackToJail(player: Player) {
        scope.launch {
            jailApi.getInmate(player.uniqueId.toString())
                .getOrNull()
                ?: return@launch
            jailController.tryTeleportToJail(player.uniqueId)
            player.sendMessage(translation.jails.youInJail.component)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onJoin(e: PlayerJoinEvent) {
        teleportBackToJail(e.player)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onRespawn(e: PlayerRespawnEvent) {
        teleportBackToJail(e.player)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("UnusedParameter")
    fun onStart(e: ServerLoadEvent) {
        Bukkit.getOnlinePlayers()
            .forEach { player -> cachedJailApi.cache(player) }
    }
}
