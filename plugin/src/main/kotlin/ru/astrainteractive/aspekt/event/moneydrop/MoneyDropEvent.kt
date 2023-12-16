package ru.astrainteractive.aspekt.event.moneydrop

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import ru.astrainteractive.aspekt.event.moneydrop.di.MoneyDropDependencies
import ru.astrainteractive.astralibs.event.DSLEvent

class MoneyDropEvent(
    dependencies: MoneyDropDependencies
) : MoneyDropDependencies by dependencies {

    val entityDeathEvent = DSLEvent<EntityDamageByEntityEvent>(eventListener, plugin) { e ->
        val player = e.damager as? Player ?: return@DSLEvent
        if (e.entity is Player) return@DSLEvent
        val entity = e.entity as? LivingEntity ?: return@DSLEvent
        if (entity.health > e.finalDamage) return@DSLEvent
        moneyDropController.tryDrop(entity.location, entity.type.name)
    }

    val playerPickUpEvent = DSLEvent<EntityPickupItemEvent>(eventListener, plugin) { e ->
        val player = e.entity as? Player ?: return@DSLEvent
        val item = e.item.itemStack
        val amount = e.item.itemStack.amount
        if (!moneyDropController.isMoneyDropItem(item)) return@DSLEvent
        val money = moneyDropController.getMoneyAmount(item) ?: return@DSLEvent
        e.item.remove()
        e.isCancelled = true
        economyProvider?.addMoney(player.uniqueId, money * amount)
        with(translationContext) {
            player.sendMessage(translation.general.pickedUpMoney(amount * money))
        }
    }

    val inventoryMoveEvent = DSLEvent<InventoryMoveItemEvent>(eventListener, plugin) { e ->
        if (!moneyDropController.isMoneyDropItem(e.item)) return@DSLEvent
        e.isCancelled = true
    }

    val inventoryPickupItemEvent = DSLEvent<InventoryPickupItemEvent>(eventListener, plugin) { e ->
        if (!moneyDropController.isMoneyDropItem(e.item.itemStack)) return@DSLEvent
        e.isCancelled = true
    }
    val blockBreakEvent = DSLEvent<BlockBreakEvent>(eventListener, plugin) { e ->
        moneyDropController.tryDrop(e.block.location, e.block.type.name)
    }
}
