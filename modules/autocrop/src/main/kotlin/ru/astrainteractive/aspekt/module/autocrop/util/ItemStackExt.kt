package ru.astrainteractive.aspekt.module.autocrop.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

internal fun ItemStack.applyHoeDamage(): Result<Unit> {
    val damageable = itemMeta as? Damageable ?: return Result.success(Unit)
    if (damageable.damage > type.maxDurability) return Result.failure(Error())
    damageable.damage += 1
    itemMeta = damageable
    return Result.success(Unit)
}

@Suppress("MagicNumber")
internal fun ItemStack.hoeRadius(): Int {
    return when (type) {
        Material.WOODEN_HOE -> 2
        Material.STONE_HOE -> 3
        Material.IRON_HOE -> 4
        Material.GOLDEN_HOE -> 5
        Material.DIAMOND_HOE -> 6
        Material.NETHERITE_HOE -> 7
        else -> 1
    }
}
