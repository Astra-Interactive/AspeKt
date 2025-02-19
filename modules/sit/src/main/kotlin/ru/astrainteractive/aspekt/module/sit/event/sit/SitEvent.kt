package ru.astrainteractive.aspekt.module.sit.event.sit

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDismountEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector
import ru.astrainteractive.aspekt.module.sit.event.sit.di.SitDependencies
import ru.astrainteractive.astralibs.event.EventListener

class SitEvent(
    dependencies: SitDependencies
) : SitDependencies by dependencies, EventListener {

    @EventHandler
    fun onDeathEvent(e: PlayerDeathEvent) {
        sitController.stopSitPlayer(e.entity)
    }

    @EventHandler
    fun onTeleportEvent(e: PlayerTeleportEvent) {
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

    @EventHandler
    fun playerInteractEvent(e: PlayerInteractEvent) {
        if (!configuration.sit) return
        if (e.hand != EquipmentSlot.HAND) return
        if (e.player.isSneaking) return
        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        if (e.player.inventory.itemInMainHand.type != Material.AIR) return
        val material = e.clickedBlock?.type ?: return
        val sitBlockType = sitBlockType(material) ?: return
        val blockLocation = e.clickedBlock?.location?.clone() ?: return
        val offset = when (sitBlockType) {
            SitBlockEnum.STAIRS -> Vector(0.5, 0.2, 0.5)
            SitBlockEnum.SLAB -> Vector(0.5, 0.2, 0.5)
        }
        sitController.toggleSitPlayer(
            e.player,
            blockLocation.add(offset)
        )
    }

    @EventHandler
    fun onDisconnect(e: PlayerQuitEvent) {
        sitController.stopSitPlayer(e.player)
    }

    @EventHandler
    fun onDismount(e: EntityDismountEvent) {
        if (e.entity !is Player) return
        sitController.stopSitPlayer(e.entity as Player)
    }
}
