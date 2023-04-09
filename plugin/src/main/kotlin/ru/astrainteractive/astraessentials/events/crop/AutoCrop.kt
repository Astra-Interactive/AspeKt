package ru.astrainteractive.astraessentials.events.crop

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astraessentials.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import kotlin.random.Random


class AutoCrop(
    pluginConfigDep: Dependency<PluginConfiguration>
) {
    private val pluginConfiguration by pluginConfigDep
    private val controller = CropDupeController(pluginConfigDep)

    val onCropInteract = DSLEvent.event<PlayerInteractEvent> { e ->
        val autoCropConfig = pluginConfiguration.autoCrop
        if (!autoCropConfig.enabled) return@event

        if (e.action != Action.RIGHT_CLICK_BLOCK) return@event
        val clickedBlock = e.clickedBlock ?: return@event
        if (clickedBlock.type == Material.AIR) return@event
        if (controller.isDupingAtLocation(clickedBlock.location)) return@event
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
        val amount = Random.nextInt(autoCropConfig.minDrop, autoCropConfig.maxDrop)

        val item = ItemStack(material ?: return@event, amount)
        clickedCrop.age = 0
        clickedBlock.setBlockData(clickedCrop, true)
        clickedBlock.location.let { loc ->
            loc.world.dropItemNaturally(loc, item)
        }
    }
}