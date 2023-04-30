@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.events.crop

import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.events.di.EventsModule
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.getValue
import kotlin.random.Random

class AutoCropEvent(
    module: EventsModule
) {
    private val plugin by module.plugin
    private val pluginConfiguration by module.configuration
    private val eventListener by module.eventListener

    private val controller = CropDupeController(module.configuration)

    val onCropInteract = DSLEvent<PlayerInteractEvent>(eventListener, plugin) { e ->
        val autoCropConfig = pluginConfiguration.autoCrop
        if (!autoCropConfig.enabled) return@DSLEvent

        if (e.action != Action.RIGHT_CLICK_BLOCK) return@DSLEvent
        val clickedBlock = e.clickedBlock ?: return@DSLEvent
        if (clickedBlock.type == Material.AIR) return@DSLEvent
        if (controller.isDupingAtLocation(clickedBlock.location)) return@DSLEvent
        val clickedCrop = (clickedBlock.blockData as? Ageable) ?: return@DSLEvent

        if (clickedCrop.age != clickedCrop.maximumAge) return@DSLEvent
        val material = when (clickedCrop.material) {
            Material.POTATOES -> Material.POTATO
            Material.CARROTS -> Material.CARROT
            Material.BEETROOTS -> Material.BEETROOT
            Material.SWEET_BERRIES -> Material.SWEET_BERRIES
            Material.WHEAT -> Material.WHEAT
            else -> null
        }
        val amount = Random.nextInt(autoCropConfig.minDrop, autoCropConfig.maxDrop)

        val item = ItemStack(material ?: return@DSLEvent, amount)
        clickedCrop.age = 0
        clickedBlock.setBlockData(clickedCrop, true)
        clickedBlock.location.let { loc ->
            loc.world.dropItemNaturally(loc, item)
        }
    }
}
