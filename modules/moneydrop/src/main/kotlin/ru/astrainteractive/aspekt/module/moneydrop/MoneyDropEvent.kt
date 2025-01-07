package ru.astrainteractive.aspekt.module.moneydrop

import kotlinx.coroutines.launch
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import ru.astrainteractive.aspekt.module.moneydrop.di.MoneyDropDependencies
import ru.astrainteractive.astralibs.event.EventListener

internal class MoneyDropEvent(
    dependencies: MoneyDropDependencies
) : MoneyDropDependencies by dependencies, EventListener {

    @EventHandler(ignoreCancelled = true)
    fun entityDeathEvent(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        if (e.entity is Player) return
        val entity = e.entity as? LivingEntity ?: return
        if (entity.health > e.finalDamage) return
        if (e.isCancelled) return
        moneyDropController.tryDrop(entity.location, entity.type.name)
    }

    @EventHandler(ignoreCancelled = true)
    fun playerPickUpEvent(e: EntityPickupItemEvent) {
        val player = e.entity as? Player ?: return
        val item = e.item.itemStack
        val amount = e.item.itemStack.amount
        if (!moneyDropController.isMoneyDropItem(item)) return
        val money = moneyDropController.getMoneyAmount(item) ?: return
        e.item.remove()
        e.isCancelled = true
        scope.launch {
            val economyProvider = when (val currencyId = moneyDropController.getMoneyCurrency(item)) {
                null -> currencyEconomyProviderFactory.findDefault()
                else -> currencyEconomyProviderFactory.findByCurrencyId(currencyId)
            }
            economyProvider?.addMoney(player.uniqueId, money * amount)
        }
        translation.general.pickedUpMoney(amount * money)
            .let(kyoriComponentSerializer::toComponent)
            .run(player::sendMessage)
    }

    @EventHandler(ignoreCancelled = true)
    fun inventoryMoveEvent(e: InventoryMoveItemEvent) {
        if (!moneyDropController.isMoneyDropItem(e.item)) return
        e.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun inventoryPickupItemEvent(e: InventoryPickupItemEvent) {
        if (!moneyDropController.isMoneyDropItem(e.item.itemStack)) return
        e.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun blockBreakEvent(e: BlockBreakEvent) {
        if (e.isCancelled) return
        moneyDropController.tryDrop(e.block.location, e.block.type.name)
    }

    @EventHandler(ignoreCancelled = true)
    fun blockPlaceEvent(e: BlockPlaceEvent) {
        moneyDropController.rememberLocation(e.blockPlaced.location, e.blockPlaced.type.name)
    }
}
