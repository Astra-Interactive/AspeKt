package ru.astrainteractive.aspekt.events.sort

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.astrainteractive.aspekt.events.di.EventsModule
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.getValue

class SortEvent(
    module: EventsModule
) {
    private val plugin by module.plugin
    private val eventListener by module.eventListener
    private val sortController = SortController()

    val playerQuit = DSLEvent<PlayerQuitEvent>(eventListener, plugin) { e ->
        sortController.rememberPlayer(e.player)
    }
    val playerJoin = DSLEvent<PlayerJoinEvent>(eventListener, plugin) { e ->
        sortController.removePlayer(e.player)
    }
    val inventoryClick = DSLEvent<InventoryClickEvent>(eventListener, plugin) { e ->
        if (e.click != ClickType.MIDDLE) return@DSLEvent
        if (!e.isShiftClick) return@DSLEvent
        val clickedInventory = e.clickedInventory ?: return@DSLEvent
        val player = e.whoClicked as? Player ?: return@DSLEvent
        sortController.trySortInventory(clickedInventory, player)
    }
}
