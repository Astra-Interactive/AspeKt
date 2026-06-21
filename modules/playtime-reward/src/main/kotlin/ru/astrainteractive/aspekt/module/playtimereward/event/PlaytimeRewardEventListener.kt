package ru.astrainteractive.aspekt.module.playtimereward.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.astrainteractive.aspekt.module.playtimereward.controller.PlaytimeRewardController
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer

internal class PlaytimeRewardEventListener(
    private val controller: PlaytimeRewardController,
    private val ioScope: CoroutineScope
) : EventListener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ioScope.launch {
            controller.onPlayerJoin(event.player.asOnlineMinecraftPlayer())
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        ioScope.launch {
            controller.onPlayerQuit(event.player.asOnlineMinecraftPlayer())
        }
    }
}
