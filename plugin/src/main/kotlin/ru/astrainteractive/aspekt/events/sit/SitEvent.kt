@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.events.sit

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import org.spigotmc.event.entity.EntityDismountEvent
import ru.astrainteractive.aspekt.events.di.EventsModule
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.getValue

class SitEvent(
    module: EventsModule
) {
    private val sitController by module.sitController
    private val pluginConfiguration by module.configuration
    private val eventListener by module.eventListener
    private val plugin by module.plugin

    val onDeathEvent = DSLEvent<PlayerDeathEvent>(eventListener, plugin) { e ->
        sitController.stopSitPlayer(e.entity)
    }

    val onTeleportEvent = DSLEvent<PlayerTeleportEvent>(eventListener, plugin) { e ->
        sitController.stopSitPlayer(e.player)
    }

    val playerInteractEvent = DSLEvent<PlayerInteractEvent>(eventListener, plugin) { e ->
        if (!pluginConfiguration.sit) return@DSLEvent
        if (e.action != Action.RIGHT_CLICK_BLOCK) {
            return@DSLEvent
        }
        if (e.player.inventory.itemInMainHand.type != Material.AIR) {
            return@DSLEvent
        }
        if (e.clickedBlock?.type?.name?.contains("stairs", ignoreCase = true) == true) {
            sitController.toggleSitPlayer(
                e.player,
                e.clickedBlock?.location?.clone()?.add(0.5, 0.5, 0.5) ?: return@DSLEvent
            )
        }
    }

    val onDisconnect = DSLEvent<PlayerQuitEvent>(eventListener, plugin) { e ->
        sitController.stopSitPlayer(e.player)
    }
    val onDismount = DSLEvent<EntityDismountEvent>(eventListener, plugin) { e ->
        if (e.entity !is Player) return@DSLEvent
        sitController.stopSitPlayer(e.entity as Player)
    }
}
