package ru.astrainteractive.astraessentials.events.sort

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent

class SortEvent(
    sortControllerDependency: Dependency<SortController>
) {
    private val sortController by sortControllerDependency
    val playerQuit = DSLEvent.event<PlayerQuitEvent> { e ->
        sortController.rememberPlayer(e.player)
    }
    val playerJoin = DSLEvent.event<PlayerJoinEvent> { e ->
        sortController.removePlayer(e.player)
    }
    val inventoryClick = DSLEvent.event<InventoryClickEvent> { e ->
        if (e.click != ClickType.MIDDLE) return@event
        val clickedInventory = e.clickedInventory ?: return@event
        sortController.trySortInventory(clickedInventory, e.whoClicked as? Player ?: return@event)
    }
}