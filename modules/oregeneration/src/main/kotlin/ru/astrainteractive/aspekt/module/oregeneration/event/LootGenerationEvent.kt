package ru.astrainteractive.aspekt.module.oregeneration.event

import org.bukkit.event.EventHandler
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.module.oregeneration.model.LootGenerationConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import kotlin.random.Random

internal class LootGenerationEvent(
    configKrate: CachedKrate<LootGenerationConfiguration>,
) : EventListener {
    private val configuration by configKrate

    private fun newAmount(
        itemStack: ItemStack,
        items: Map<String, Double>
    ): Int {
        val removeChance = items[itemStack.type.name] ?: return itemStack.amount
        val amount = itemStack.amount
        if (amount <= 0) return 0
        if (removeChance <= 0.0) return amount
        val removedCount = (0 until amount).count { Random.nextDouble() < removeChance }
        return amount.minus(removedCount).takeIf { amount -> amount > 0 } ?: 0
    }

    @EventHandler
    fun onLootGenerate(event: LootGenerateEvent) {
        val currentConfiguration = configuration
        if (!currentConfiguration.enabled) return
        val items = currentConfiguration.items
        if (items.isEmpty()) return

        event.loot.filterNotNull().forEach { itemStack ->
            val newAmount = newAmount(itemStack, items)
            itemStack.amount = newAmount
        }
    }
}
