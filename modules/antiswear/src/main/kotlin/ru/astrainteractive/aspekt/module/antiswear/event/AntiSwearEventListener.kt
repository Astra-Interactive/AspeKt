package ru.astrainteractive.aspekt.module.antiswear.event

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import ru.astrainteractive.astralibs.event.EventListener

internal class AntiSwearEventListener : EventListener {
    @EventHandler
    fun onMessage(e: AsyncChatEvent) {
        val swearRenderer = SwearRenderer(e.renderer())
        e.renderer(swearRenderer)
    }
}
