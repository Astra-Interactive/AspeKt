package ru.astrainteractive.aspekt.event.crop

import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.event.crop.di.AutoCropDependencies
import ru.astrainteractive.astralibs.event.DSLEvent
import kotlin.random.Random

class AutoCropEvent(
    module: AutoCropDependencies
) : AutoCropDependencies by module {

    val onCropInteract = DSLEvent<PlayerInteractEvent>(eventListener, plugin) { e ->
        val autoCropConfig = configuration.autoCrop
        if (!autoCropConfig.enabled) return@DSLEvent

        if (e.action != Action.RIGHT_CLICK_BLOCK) return@DSLEvent
        val clickedBlock = e.clickedBlock ?: return@DSLEvent
        if (clickedBlock.type == Material.AIR) return@DSLEvent
        val clickedCrop = (clickedBlock.blockData as? Ageable) ?: return@DSLEvent

        if (clickedCrop.age != clickedCrop.maximumAge) return@DSLEvent

        val material = when (clickedCrop.material) {
            Material.POTATOES -> Material.POTATO
            Material.CARROTS -> Material.CARROT
            Material.BEETROOTS -> Material.BEETROOT
            Material.SWEET_BERRIES -> Material.SWEET_BERRIES
            Material.WHEAT -> Material.WHEAT
            else -> null
        } ?: return@DSLEvent
        val amount = when {
            cropDupeController.isDupingAtLocation(clickedBlock.location) -> 1
            else -> Random.nextInt(autoCropConfig.min, autoCropConfig.max)
        }

        val item = ItemStack(material, amount)
        clickedCrop.age = 0
        clickedBlock.setBlockData(clickedCrop, true)
        clickedBlock.location.let { loc ->
            loc.world.dropItemNaturally(loc, item)
        }
    }
}
