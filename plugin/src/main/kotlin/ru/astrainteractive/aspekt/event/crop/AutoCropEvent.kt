package ru.astrainteractive.aspekt.event.crop

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import ru.astrainteractive.aspekt.event.crop.di.AutoCropDependencies
import ru.astrainteractive.astralibs.event.DSLEvent
import kotlin.random.Random

class AutoCropEvent(
    module: AutoCropDependencies
) : AutoCropDependencies by module {

    private fun getRelativeBlocks(block: Block, hoe: ItemStack, radius: Int): Set<Block> {
        return if (radius <= 1) {
            setOf(block)
        } else {
            BlockFace.values()
                .flatMap { blockFace -> getRelativeBlocks(block.getRelative(blockFace), hoe, radius - 1) }
                .toSet()
        }
    }

    private fun tryApplyHoeDamage(hoeItemStack: ItemStack?): Result<Unit> {
        hoeItemStack ?: return Result.success(Unit)
        val damageable = hoeItemStack.itemMeta as? Damageable ?: return Result.success(Unit)
        if (damageable.damage > hoeItemStack.type.maxDurability) return Result.failure(Error())
        damageable.damage += 1
        hoeItemStack.itemMeta = damageable
        return Result.success(Unit)
    }

    private fun cropMaterialOrNull(material: Material): Material? {
        return when (material) {
            Material.POTATOES -> Material.POTATO
            Material.CARROTS -> Material.CARROT
            Material.BEETROOTS -> Material.BEETROOT
            Material.SWEET_BERRIES -> Material.SWEET_BERRIES
            Material.WHEAT -> Material.WHEAT
            else -> null
        }
    }

    private fun processBlock(block: Block, hoeItemStack: ItemStack?) {
        val autoCropConfig = configuration.autoCrop
        if (block.type == Material.AIR) return
        val clickedCrop = (block.blockData as? Ageable) ?: return

        if (clickedCrop.age != clickedCrop.maximumAge) return
        tryApplyHoeDamage(hoeItemStack).onFailure { return }

        val material = cropMaterialOrNull(clickedCrop.material) ?: return

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
        val radius = when (hoeMaybe.type) {
            Material.WOODEN_HOE -> 2
            Material.STONE_HOE -> 3
            Material.IRON_HOE -> 4
            Material.GOLDEN_HOE -> 5
            Material.DIAMOND_HOE -> 6
            Material.NETHERITE_HOE -> 7
            else -> 1
        }
        val hoeItemStack = hoeMaybe.takeIf { radius > 1 }
        getRelativeBlocks(clickedBlock, hoeMaybe, radius)
            .forEach { block ->
                processBlock(block, hoeItemStack)
            }
    }
}
