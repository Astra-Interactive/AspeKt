package ru.astrainteractive.aspekt.event.sort

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.astrainteractive.aspekt.event.sort.di.SortDependencies
import ru.astrainteractive.astralibs.event.DSLEvent

class SortEvent(
    module: SortDependencies
) : SortDependencies by module {
    val playerQuit = DSLEvent<PlayerQuitEvent>(eventListener, plugin) { e ->
        sortController.rememberPlayer(e.player)
    }
    val playerJoin = DSLEvent<PlayerJoinEvent>(eventListener, plugin) { e ->
        sortController.removePlayer(e.player)
    }
    val inventoryClick = DSLEvent<InventoryClickEvent>(eventListener, plugin) { e ->
        if (e.click != ClickType.SHIFT_RIGHT) return@DSLEvent
        val clickedInventory = e.clickedInventory ?: return@DSLEvent
        val player = e.whoClicked as? Player ?: return@DSLEvent
        e.isCancelled = true
        sortController.trySortInventory(clickedInventory, player)
    }
}
