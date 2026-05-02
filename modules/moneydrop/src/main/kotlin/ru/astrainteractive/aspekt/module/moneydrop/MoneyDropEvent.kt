package ru.astrainteractive.aspekt.module.moneydrop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class MoneyDropEvent(
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<PluginTranslation>,
    val moneyDropController: MoneyDropController,
    val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    val ioScope: CoroutineScope
) : EventListener,
    Logger by JUtiltLogger("AspeKt-MoneyDropEvent") {
    private val translation by translationKrate
    private val kyori by kyoriKrate

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun entityDeathEvent(e: EntityDeathEvent) {
        if (e.isCancelled) return
        if (e.entity is Player) return
        listOf(
            e.damageSource.directEntity,
            e.damageSource.causingEntity
        ).filterIsInstance<Player>().firstOrNull() ?: return
        val entity = e.entity
        if (!entity.isDead) return
        moneyDropController.tryDrop(entity.location, entity.type.name)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun playerPickUpEvent(e: EntityPickupItemEvent) {
        val player = e.entity as? Player ?: return
        val item = e.item.itemStack
        val amount = e.item.itemStack.amount
        if (!moneyDropController.isMoneyDropItem(item)) return
        val money = moneyDropController.getMoneyAmount(item) ?: return
        e.item.remove()
        e.isCancelled = true
        ioScope.launch {
            val economyProvider = when (val currencyId = moneyDropController.getMoneyCurrency(item)) {
                null -> currencyEconomyProviderFactory.findDefault()
                else -> currencyEconomyProviderFactory.findByCurrencyId(currencyId)
            }
            economyProvider?.addMoney(player.uniqueId, money * amount)
        }
        translation.general.pickedUpMoney(amount * money)
            .let(kyori::toComponent)
            .run(player::sendMessage)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun inventoryMoveEvent(e: InventoryMoveItemEvent) {
        if (!moneyDropController.isMoneyDropItem(e.item)) return
        e.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun inventoryPickupItemEvent(e: InventoryPickupItemEvent) {
        if (!moneyDropController.isMoneyDropItem(e.item.itemStack)) return
        e.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun blockBreakEvent(e: BlockBreakEvent) {
        if (e.isCancelled) return
        moneyDropController.tryDrop(e.block.location, e.block.type.name)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun blockPlaceEvent(e: BlockPlaceEvent) {
        moneyDropController.rememberLocation(e.blockPlaced.location, e.blockPlaced.type.name)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun blockPistonRetractEvent(e: BlockPistonRetractEvent) {
        e.blocks.plus(e.block).forEach { block ->
            moneyDropController.rememberLocation(block.location, block.type.name)
            val relative = block.getRelative(e.direction)
            moneyDropController.rememberLocation(relative.location, block.type.name)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun blockPistonExtendEvent(e: BlockPistonExtendEvent) {
        e.blocks.plus(e.block).forEach { block ->
            moneyDropController.rememberLocation(block.location, block.type.name)
            val relative = block.getRelative(e.direction)
            moneyDropController.rememberLocation(relative.location, block.type.name)
        }
    }
}
