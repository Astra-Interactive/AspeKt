package ru.astrainteractive.aspekt.event.crop.domain

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

interface HoeDamager {
    fun applyDamage(hoeItemStack: ItemStack): Result<Unit>
}

class HoeDamagerImpl : HoeDamager {
    override fun applyDamage(hoeItemStack: ItemStack): Result<Unit> {
        val damageable = hoeItemStack.itemMeta as? Damageable ?: return Result.success(Unit)
        if (damageable.damage > hoeItemStack.type.maxDurability) return Result.failure(Error())
        damageable.damage += 1
        hoeItemStack.itemMeta = damageable
        return Result.success(Unit)
    }
}
