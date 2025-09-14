package ru.astrainteractive.aspekt.event

import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import java.util.UUID

class ProxyPingEventListener(
    private val onlineSimulator: OnlineSimulator
) : KListener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onProxyPingEvent(event: ProxyPingEvent) {
        val fakeSamplePlayerCount = onlineSimulator.currentOnlineFlow.value
        event.response.players.sample = List(fakeSamplePlayerCount) {
            ServerPing.PlayerInfo("Empire$it", UUID.randomUUID())
        }.toTypedArray()
        event.response.players.max = fakeSamplePlayerCount + 5
        event.response.players.online = fakeSamplePlayerCount
    }
}
