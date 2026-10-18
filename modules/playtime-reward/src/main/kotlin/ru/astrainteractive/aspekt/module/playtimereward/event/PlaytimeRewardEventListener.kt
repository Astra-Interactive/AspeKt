package ru.astrainteractive.aspekt.module.playtimereward.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeEvent
import ru.astrainteractive.aspekt.module.playtimereward.usecase.TrackPlaytimeUseCase
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer

internal class PlaytimeRewardEventListener(
    private val trackPlaytimeUseCase: TrackPlaytimeUseCase,
    private val ioScope: CoroutineScope
) : EventListener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ioScope.launch {
            trackPlaytimeUseCase.invoke(event.player.asOnlineMinecraftPlayer(), PlaytimeEvent.Joined)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        ioScope.launch {
            trackPlaytimeUseCase.invoke(event.player.asOnlineMinecraftPlayer(), PlaytimeEvent.Left)
        }
    }
}
