package ru.astrainteractive.aspekt.module.antiswear.event

import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.astralibs.event.EventListener

internal class AntiSwearEventListener(
    private val swearRepository: SwearRepository,
    private val scope: CoroutineScope
) : EventListener {
    @EventHandler
    fun onMessage(e: AsyncChatEvent) {
        val swearRenderer = SwearRenderer(
            renderer = e.renderer(),
            swearRepository = swearRepository
        )
        e.renderer(swearRenderer)
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        scope.launch {
            swearRepository.rememberPlayer(e.player)
        }
    }

    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        scope.launch {
            swearRepository.forgetPlayer(e.player)
        }
    }

    private fun preHeatPlayers() = scope.launch {
        Bukkit.getOnlinePlayers().map { player ->
            async { swearRepository.rememberPlayer(player) }
        }.awaitAll()
    }

    override fun onEnable(plugin: Plugin) {
        super.onEnable(plugin)
        preHeatPlayers()
    }
}
