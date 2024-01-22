package ru.astrainteractive.aspekt.module.autocrop.domain

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

internal interface HoeRadiusFactory {
    fun create(hoeMaybe: ItemStack): Int
}

internal class HoeRadiusFactoryImpl : HoeRadiusFactory {
    override fun create(hoeMaybe: ItemStack): Int {
        return when (hoeMaybe.type) {
            Material.WOODEN_HOE -> 2
            Material.STONE_HOE -> 3
            Material.IRON_HOE -> 4
            Material.GOLDEN_HOE -> 5
            Material.DIAMOND_HOE -> 6
            Material.NETHERITE_HOE -> 7
            else -> 1
        }
    }
}
