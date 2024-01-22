package ru.astrainteractive.aspekt.module.autocrop.domain

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

internal interface HoeDamager {
    fun applyDamage(hoeItemStack: ItemStack): Result<Unit>
}

internal class HoeDamagerImpl : HoeDamager {
    override fun applyDamage(hoeItemStack: ItemStack): Result<Unit> {
        val damageable = hoeItemStack.itemMeta as? Damageable ?: return Result.success(Unit)
        if (damageable.damage > hoeItemStack.type.maxDurability) return Result.failure(Error())
        damageable.damage += 1
        hoeItemStack.itemMeta = damageable
        return Result.success(Unit)
    }
}
