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
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.events.GlobalEventListener

class SitEvent(
    sitControllerDependency: Dependency<SitController>,
    pluginConfigurationDep: Dependency<PluginConfiguration>,
    private val bukkitDispatchers: BukkitDispatchers
) {
    private val plugin by AspeKt
    private val sitController by sitControllerDependency
    private val pluginConfiguration by pluginConfigurationDep

    val onDeathEvent = DSLEvent<PlayerDeathEvent>(GlobalEventListener, plugin) { e ->
        sitController.stopSitPlayer(e.entity)
    }

    val onTeleportEvent = DSLEvent<PlayerTeleportEvent>(GlobalEventListener, plugin) { e ->
        sitController.stopSitPlayer(e.player)
    }

    val playerInteractEvent = DSLEvent<PlayerInteractEvent>(GlobalEventListener, plugin) { e ->
        if (!pluginConfiguration.sit) return@DSLEvent
        if (e.action != Action.RIGHT_CLICK_BLOCK)
            return@DSLEvent
        if (e.player.inventory.itemInMainHand.type != Material.AIR)
            return@DSLEvent
        if (e.clickedBlock?.type?.name?.contains("stairs", ignoreCase = true) == true)
            sitController.toggleSitPlayer(
                e.player,
                e.clickedBlock?.location?.clone()?.add(0.5, 0.5, 0.5) ?: return@DSLEvent
            )

    }

    val onDisconnect = DSLEvent<PlayerQuitEvent>(GlobalEventListener, plugin) { e ->
        sitController.stopSitPlayer(e.player)
    }
    val onDismount = DSLEvent<EntityDismountEvent>(GlobalEventListener, plugin) { e ->
        if (e.entity !is Player) return@DSLEvent
        sitController.stopSitPlayer(e.entity as Player)
    }
}