package ru.astrainteractive.aspekt.module.autocrop

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.module.autocrop.di.AutoCropDependencies
import ru.astrainteractive.astralibs.event.DSLEvent
import kotlin.random.Random

internal class AutoCropEvent(
    module: AutoCropDependencies
) : AutoCropDependencies by module {

    private fun processBlock(block: Block, hoeItemStack: ItemStack?) {
        val autoCropConfig = configuration.autoCrop
        if (block.type == Material.AIR) return
        val clickedCrop = (block.blockData as? Ageable) ?: return

        if (clickedCrop.age != clickedCrop.maximumAge) return
        hoeItemStack?.let(hoeDamager::applyDamage)?.onFailure { return }

        val material = cropMaterialMapper.toMaterial(clickedCrop.material) ?: return

        val amount = when {
            cropDupeController.isDupingAtLocation(block.location) -> 1
            else -> Random.nextInt(autoCropConfig.min, autoCropConfig.max)
        }

        val item = ItemStack(material, amount)
        clickedCrop.age = 0
        block.setBlockData(clickedCrop, true)
        block.location.let { loc ->
            loc.world.dropItemNaturally(loc, item)
        }
    }

    val onCropInteract = DSLEvent<PlayerInteractEvent>(eventListener, plugin) { e ->
        val autoCropConfig = configuration.autoCrop
        if (!autoCropConfig.enabled) return@DSLEvent

        if (e.action != Action.RIGHT_CLICK_BLOCK) return@DSLEvent
        val clickedBlock = e.clickedBlock ?: return@DSLEvent
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
