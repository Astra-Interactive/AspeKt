package ru.astrainteractive.aspekt.invisibleframes.event

import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEntityEvent
import ru.astrainteractive.astralibs.event.EventListener

internal class InvisibleFramesEvent : EventListener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun playerInteractEvent(e: PlayerInteractEntityEvent) {
        if (e.isCancelled) return
        if (!e.player.isSneaking) return
        val itemFrame = e.rightClicked as? ItemFrame ?: return
        when {
            itemFrame.isVisible && itemFrame.isGlowing -> {
                itemFrame.isGlowing = false
            }

            itemFrame.isVisible -> {
                itemFrame.isVisible = false
            }

            !itemFrame.isVisible && !itemFrame.isGlowing -> {
                itemFrame.isGlowing = true
            }

            !itemFrame.isVisible && itemFrame.isGlowing -> {
                itemFrame.isVisible = true
            }

            else -> return
        }
        e.isCancelled = true
    }
}
