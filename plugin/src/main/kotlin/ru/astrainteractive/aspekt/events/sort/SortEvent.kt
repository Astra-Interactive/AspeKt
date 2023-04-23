@file:OptIn(UnsafeApi::class)
package ru.astrainteractive.aspekt.events.sort

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.events.GlobalEventListener

class SortEvent(
    sortControllerDependency: Dependency<SortController>,
    private val bukkitDispatchers: BukkitDispatchers
) {
    private val plugin by AspeKt
    private val sortController by sortControllerDependency
    val playerQuit = DSLEvent<PlayerQuitEvent>(GlobalEventListener, plugin) { e ->
        sortController.rememberPlayer(e.player)
    }
    val playerJoin = DSLEvent<PlayerJoinEvent>(GlobalEventListener, plugin) { e ->
        sortController.removePlayer(e.player)
    }
    val inventoryClick = DSLEvent<InventoryClickEvent>(GlobalEventListener, plugin) { e ->
        if (e.click != ClickType.MIDDLE) return@DSLEvent
        if (!e.isShiftClick) return@DSLEvent
        val clickedInventory = e.clickedInventory ?: return@DSLEvent
        val player = e.whoClicked as? Player ?: return@DSLEvent
        sortController.trySortInventory(clickedInventory, player)
    }
}