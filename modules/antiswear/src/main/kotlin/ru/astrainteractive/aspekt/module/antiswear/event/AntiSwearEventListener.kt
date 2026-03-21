package ru.astrainteractive.aspekt.module.antiswear.event

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
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer

internal class AntiSwearEventListener(
    private val swearRepository: SwearRepository,
    private val scope: CoroutineScope
) : EventListener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        scope.launch {
            swearRepository.rememberPlayer(e.player.asOnlineMinecraftPlayer())
        }
    }

    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        scope.launch {
            swearRepository.forgetPlayer(e.player.asOnlineMinecraftPlayer())
        }
    }

    private fun preHeatPlayers() = scope.launch {
        Bukkit.getOnlinePlayers().map { player ->
            async { swearRepository.rememberPlayer(player.asOnlineMinecraftPlayer()) }
        }.awaitAll()
    }

    override fun onEnable(plugin: Plugin) {
        super.onEnable(plugin)
        preHeatPlayers()
    }
}
