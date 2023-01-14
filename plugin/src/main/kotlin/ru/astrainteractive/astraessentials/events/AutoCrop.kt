package ru.astrainteractive.astraessentials.events

import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.events.DSLEvent
import kotlin.random.Random

class AutoCrop {
    val onCropInteract = DSLEvent.event<PlayerInteractEvent> { e ->
        if (e.action != Action.RIGHT_CLICK_BLOCK)
            return@event
        val clickedBlock = e.clickedBlock ?: return@event
        val loc = clickedBlock.location.clone()
        if (clickedBlock.type == Material.AIR) return@event

        val clickedCrop = (clickedBlock.blockData as? Ageable) ?: return@event

        if (clickedCrop.age != clickedCrop.maximumAge)
            return@event
        val material = when (clickedCrop.material) {
            Material.POTATOES -> Material.POTATO
            Material.CARROTS -> Material.CARROT
            Material.BEETROOTS -> Material.BEETROOT
            Material.SWEET_BERRIES -> Material.SWEET_BERRIES
            Material.WHEAT -> Material.WHEAT
            else -> null
        }
        val item = ItemStack(material ?: return@event, Random.nextInt(1, 3))
        clickedCrop.age = 0
        clickedBlock.setBlockData(clickedCrop, true)
        loc.world.dropItemNaturally(loc, item)
    }

}