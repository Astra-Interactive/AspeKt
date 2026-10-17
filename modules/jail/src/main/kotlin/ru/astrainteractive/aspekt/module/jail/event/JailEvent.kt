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
import ru.astrainteractive.aspekt.module.jail.api.JailCache
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.usecase.TeleportInmateToJailUseCase
import ru.astrainteractive.aspekt.module.jail.util.cache
import ru.astrainteractive.aspekt.module.jail.util.forget
import ru.astrainteractive.aspekt.module.jail.util.isInJail
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class JailEvent(
    private val jailRepository: JailRepository,
    private val jailCache: JailCache,
    private val teleportInmateToJailUseCase: TeleportInmateToJailUseCase,
    private val scope: CoroutineScope,
    translationKrate: CachedKrate<PluginTranslation>
) : EventListener,
    Logger by JUtiltLogger("AspeKt-JailEvent") {
    private val translation by translationKrate

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerCommandPreprocessEvent(e: PlayerCommandPreprocessEvent) {
        if (!jailCache.isInJail(e.player)) return
        e.isCancelled = true
        e.player.sendMessage(translation.jails.commandBlocked)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInteract(e: PlayerInteractEvent) {
        jailCache.cache(e.player)
        if (!jailCache.isInJail(e.player)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onQuit(e: PlayerQuitEvent) {
        jailCache.forget(e.player)
    }

    private fun teleportBackToJail(player: Player) {
        scope.launch {
            val inmate = jailRepository.getInmate(player.uniqueId.toString())
                .getOrNull()
                ?: return@launch
            teleportInmateToJailUseCase.invoke(inmate)
            player.sendMessage(translation.jails.inJail)
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
            .forEach { player -> jailCache.cache(player) }
    }
}
