package ru.astrainteractive.aspekt.inventorysort.event.sort

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.astrainteractive.aspekt.inventorysort.event.sort.di.SortDependencies
import ru.astrainteractive.astralibs.event.EventListener

class SortEvent(
    module: SortDependencies
) : SortDependencies by module, EventListener {

    @EventHandler
    fun playerQuitEvent(e: PlayerQuitEvent) {
        sortController.rememberPlayer(e.player)
    }

    @EventHandler
    fun playerJoin(e: PlayerJoinEvent) {
        sortController.removePlayer(e.player)
    }

    @EventHandler
    fun inventoryClick(e: InventoryClickEvent) {
        if (e.click != ClickType.SHIFT_RIGHT) return
        val clickedInventory = e.clickedInventory ?: return
        val player = e.whoClicked as? Player ?: return
        e.isCancelled = true
        sortController.trySortInventory(clickedInventory, player)
    }
}
