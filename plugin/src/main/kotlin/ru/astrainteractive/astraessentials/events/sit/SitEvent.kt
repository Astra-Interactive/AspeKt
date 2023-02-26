package ru.astrainteractive.astraessentials.events.sit

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.spigotmc.event.entity.EntityDismountEvent
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent

class SitEvent(
    sitControllerDependency: Dependency<SitController>
) {
    private val sitController by sitControllerDependency


    val onDeathEvent = DSLEvent.event<PlayerDeathEvent> { e ->
        sitController.stopSitPlayer(e.entity)
    }

    val onTeleportEvent = DSLEvent.event<PlayerTeleportEvent> { e ->
        sitController.stopSitPlayer(e.player)
    }

    val playerInteractEvent = DSLEvent.event<PlayerInteractEvent> { e ->
        if (e.action != Action.RIGHT_CLICK_BLOCK)
            return@event
        if (e.player.inventory.itemInMainHand.type != Material.AIR)
            return@event
        if (e.clickedBlock?.type?.name?.contains("stairs", ignoreCase = true) == true)
            sitController.toggleSitPlayer(
                e.player,
                e.clickedBlock?.location?.clone()?.add(0.5, 0.5, 0.5) ?: return@event
            )

    }

    val onDisconnect = DSLEvent.event<PlayerQuitEvent> { e ->
        sitController.stopSitPlayer(e.player)
    }
    val onDismount = DSLEvent.event<EntityDismountEvent> { e ->
        if (e.entity !is Player)
            return@event
        sitController.stopSitPlayer(e.entity as Player)
    }
}