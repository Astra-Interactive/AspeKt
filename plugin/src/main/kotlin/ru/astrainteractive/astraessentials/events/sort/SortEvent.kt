package ru.astrainteractive.astraessentials.events.sort

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.astrainteractive.astralibs.events.DSLEvent

class SortEvent {
    val playerQuit = DSLEvent.event<PlayerQuitEvent>{ e ->
        SortController.rememberPlayer(e.player)
    }
    val playerJoin = DSLEvent.event<PlayerJoinEvent>{ e ->
        SortController.removePlayer(e.player)
    }
    val inventoryClick = DSLEvent.event<InventoryClickEvent>{ e ->
        if (e.click != ClickType.MIDDLE) return@event
        val clickedInventory = e.clickedInventory ?: return@event
        SortController.trySortInventory(clickedInventory,e.whoClicked as? Player ?: return@event)
    }
}