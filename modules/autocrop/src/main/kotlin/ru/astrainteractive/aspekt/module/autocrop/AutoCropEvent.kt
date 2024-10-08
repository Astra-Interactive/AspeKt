package ru.astrainteractive.aspekt.module.autocrop

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.module.autocrop.di.AutoCropDependencies
import ru.astrainteractive.astralibs.event.EventListener
import kotlin.random.Random

internal class AutoCropEvent(
    module: AutoCropDependencies
) : AutoCropDependencies by module, EventListener {

    private fun processBlock(block: Block, hoeItemStack: ItemStack?) {
        val autoCropConfig = configuration.autoCrop
        if (block.type == Material.AIR) return
        val clickedCrop = (block.blockData as? Ageable) ?: return

        if (clickedCrop.age != clickedCrop.maximumAge) return
        hoeItemStack?.let(hoeDamager::applyDamage)?.onFailure { return }

        val cropMaterial = cropMaterialMapper.toMaterial(clickedCrop.material) ?: return
        val cropSeedMaterial = cropSeedMaterialMapper.toMaterial(clickedCrop.material)

        val amount = when {
            cropDupeController.isDupingAtLocation(block.location) -> 1
            else -> Random.nextInt(autoCropConfig.min, autoCropConfig.max)
        }

        clickedCrop.age = 0
        block.setBlockData(clickedCrop, true)
        val location = block.location
        val cropItemStack = ItemStack(cropMaterial, amount)
        location.world.dropItemNaturally(location, cropItemStack)

        if (cropSeedMaterial != null) {
            val seedItemStack = ItemStack(cropSeedMaterial, amount)
            location.world.dropItemNaturally(location, seedItemStack)
        }
    }

    @EventHandler
    fun onCropInteract(e: PlayerInteractEvent) {
        val autoCropConfig = configuration.autoCrop
        if (!autoCropConfig.enabled) return

        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        val clickedBlock = e.clickedBlock ?: return
        val hoeMaybe = e.player.inventory.itemInMainHand
        val radius = hoeRadiusFactory.create(hoeMaybe)
        val hoeItemStack = hoeMaybe.takeIf { radius > 1 }
        createRelativeBlockProvider()
            .provide(clickedBlock, radius)
            .forEach { block ->
                processBlock(block, hoeItemStack)
            }
    }
}
