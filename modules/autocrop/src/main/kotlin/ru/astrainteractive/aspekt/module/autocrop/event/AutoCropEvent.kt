package ru.astrainteractive.aspekt.module.autocrop.event

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.module.autocrop.model.AutoCropConfiguration
import ru.astrainteractive.aspekt.module.autocrop.util.applyHoeDamage
import ru.astrainteractive.aspekt.module.autocrop.util.hoeRadius
import ru.astrainteractive.aspekt.module.autocrop.util.relativeBlocks
import ru.astrainteractive.aspekt.module.autocrop.util.toCropMaterial
import ru.astrainteractive.aspekt.module.autocrop.util.toCropSeedMaterial
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import kotlin.random.Random

internal class AutoCropEvent(
    pluginConfig: CachedKrate<AutoCropConfiguration>,
    private val cropDupeController: CropDupeController
) : EventListener {
    private val autoCropConfig by pluginConfig

    private fun harvestCrop(block: Block, hoeItemStack: ItemStack?) {
        if (block.type == Material.AIR) return
        val clickedCrop = (block.blockData as? Ageable) ?: return

        if (clickedCrop.age != clickedCrop.maximumAge) return
        hoeItemStack?.applyHoeDamage()?.onFailure { return }

        val cropMaterial = clickedCrop.material.toCropMaterial() ?: return
        val cropSeedMaterial = clickedCrop.material.toCropSeedMaterial()

        val amount = when {
            cropDupeController.isDupingAtLocation(block.location) -> 1
            else -> Random.Default.nextInt(autoCropConfig.min, autoCropConfig.max)
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
        if (!autoCropConfig.isEnabled) return

        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        val clickedBlock = e.clickedBlock ?: return
        val hoeMaybe = e.player.inventory.itemInMainHand
        val radius = hoeMaybe.hoeRadius()
        val hoeItemStack = hoeMaybe.takeIf { radius > 1 }
        clickedBlock
            .relativeBlocks(radius)
            .forEach { block ->
                harvestCrop(block, hoeItemStack)
            }
    }
}
