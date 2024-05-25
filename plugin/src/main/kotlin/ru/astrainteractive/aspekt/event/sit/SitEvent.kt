package ru.astrainteractive.aspekt.event.sit

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDismountEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector
import ru.astrainteractive.aspekt.event.sit.di.SitDependencies
import ru.astrainteractive.astralibs.event.DSLEvent

class SitEvent(
    module: SitDependencies
) : SitDependencies by module {
    val onDeathEvent = DSLEvent<PlayerDeathEvent>(eventListener, plugin) { e ->
        sitController.stopSitPlayer(e.entity)
    }

    val onTeleportEvent = DSLEvent<PlayerTeleportEvent>(eventListener, plugin) { e ->
        sitController.stopSitPlayer(e.player)
    }

    private enum class SitBlockEnum {
        STAIRS, SLAB
    }

    private fun sitBlockType(type: Material): SitBlockEnum? {
        if (type.name.contains(other = "stairs", ignoreCase = true)) return SitBlockEnum.STAIRS
        if (type.name.contains(other = "slab", ignoreCase = true)) return SitBlockEnum.SLAB
        return null
    }

    val playerInteractEvent = DSLEvent<PlayerInteractEvent>(eventListener, plugin) { e ->
        if (!configuration.sit) return@DSLEvent
        if (e.hand != EquipmentSlot.HAND) return@DSLEvent
        if (e.player.isSneaking) return@DSLEvent
        if (e.action != Action.RIGHT_CLICK_BLOCK) return@DSLEvent
        if (e.player.inventory.itemInMainHand.type != Material.AIR) return@DSLEvent
        val material = e.clickedBlock?.type ?: return@DSLEvent
        val sitBlockType = sitBlockType(material) ?: return@DSLEvent
        val blockLocation = e.clickedBlock?.location?.clone() ?: return@DSLEvent
        val offset = when (sitBlockType) {
            SitBlockEnum.STAIRS -> Vector(0.5, 0.2, 0.5)
            SitBlockEnum.SLAB -> Vector(0.5, 0.2, 0.5)
        }
        sitController.toggleSitPlayer(
            e.player,
            blockLocation.add(offset)
        )
    }

    val onDisconnect = DSLEvent<PlayerQuitEvent>(eventListener, plugin) { e ->
        sitController.stopSitPlayer(e.player)
    }

    val onDismount = DSLEvent<EntityDismountEvent>(eventListener, plugin) { e ->
        if (e.entity !is Player) return@DSLEvent
        sitController.stopSitPlayer(e.entity as Player)
    }
}
