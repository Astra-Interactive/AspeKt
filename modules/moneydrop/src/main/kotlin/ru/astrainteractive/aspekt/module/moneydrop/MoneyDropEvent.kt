package ru.astrainteractive.aspekt.module.moneydrop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import ru.astrainteractive.aspekt.module.moneydrop.di.MoneyDropDependencies
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.event.DSLEvent

internal class MoneyDropEvent(
    dependencies: MoneyDropDependencies
) : MoneyDropDependencies by dependencies, CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO) {

    val entityDeathEvent = DSLEvent<EntityDamageByEntityEvent>(eventListener, plugin, EventPriority.MONITOR) { e ->
        val player = e.damager as? Player ?: return@DSLEvent
        if (e.entity is Player) return@DSLEvent
        val entity = e.entity as? LivingEntity ?: return@DSLEvent
        if (entity.health > e.finalDamage) return@DSLEvent
        if (e.isCancelled) return@DSLEvent
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
        launch {
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

    val inventoryMoveEvent = DSLEvent<InventoryMoveItemEvent>(eventListener, plugin) { e ->
        if (!moneyDropController.isMoneyDropItem(e.item)) return@DSLEvent
        e.isCancelled = true
    }

    val inventoryPickupItemEvent = DSLEvent<InventoryPickupItemEvent>(eventListener, plugin) { e ->
        if (!moneyDropController.isMoneyDropItem(e.item.itemStack)) return@DSLEvent
        e.isCancelled = true
    }

    val blockBreakEvent = DSLEvent<BlockBreakEvent>(eventListener, plugin, EventPriority.MONITOR) { e ->
        if (e.isCancelled) return@DSLEvent
        moneyDropController.tryDrop(e.block.location, e.block.type.name)
    }

    val blockPlaceEvent = DSLEvent<BlockPlaceEvent>(eventListener, plugin) { e ->
        moneyDropController.rememberLocation(e.blockPlaced.location)
    }
}
