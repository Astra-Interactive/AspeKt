package ru.astrainteractive.astraessentials.events

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.block.data.Ageable
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.events.DSLEvent
import kotlin.math.min
import kotlin.random.Random

class AutoCrop {

    private val locationSet = HashMap<Location, Long>()
    private var lastClear = System.currentTimeMillis()

    val onCropInteract = DSLEvent.event<PlayerInteractEvent> { e ->
        if (e.action != Action.RIGHT_CLICK_BLOCK) return@event
        val clickedBlock = e.clickedBlock ?: return@event
        if (clickedBlock.type == Material.AIR) return@event
        val clickedCrop = (clickedBlock.blockData as? Ageable) ?: return@event

        if (clickedCrop.age != clickedCrop.maximumAge) return@event
        val material = when (clickedCrop.material) {
            Material.POTATOES -> Material.POTATO
            Material.CARROTS -> Material.CARROT
            Material.BEETROOTS -> Material.BEETROOT
            Material.SWEET_BERRIES -> Material.SWEET_BERRIES
            Material.WHEAT -> Material.WHEAT
            else -> null
        }
        val luckModifier = (e.player.getAttribute(Attribute.GENERIC_LUCK)?.value ?: 0.0).coerceAtLeast(1.0)
        val luckModifierAmount = Random.nextInt((3 * luckModifier).toInt(), (8 * luckModifier).toInt())
        val lastTimeClicked = locationSet[clickedBlock.location].also {
            locationSet[clickedBlock.location] = System.currentTimeMillis()
        } ?: 0
        val amount = if (System.currentTimeMillis() - lastTimeClicked < 15_000L) 1 else luckModifierAmount
        if (System.currentTimeMillis() - lastClear > 60_000) {
            locationSet.clear()
            lastClear = System.currentTimeMillis()
        }
        val item = ItemStack(material ?: return@event, amount)
        clickedCrop.age = 0
        clickedBlock.setBlockData(clickedCrop, true)
        clickedBlock.location.let { loc ->
            loc.world.dropItemNaturally(loc, item)
        }
    }
}