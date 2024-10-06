package ru.astrainteractive.aspekt.module.menu.gui

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.InventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.StringDesc

@Suppress("TooManyFunctions")
internal class MenuGui(
    player: Player,
    private val economyProvider: EconomyFacade?,
    private val translation: PluginTranslation,
    private val menuModel: MenuModel,
    private val dispatchers: BukkitDispatchers,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) : InventoryMenu() {
    override val inventorySize: InventorySize = menuModel.size
    override val title: Component = StringDesc.Raw(menuModel.title).let(kyoriComponentSerializer::toComponent)
    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    @Suppress("VariableNaming")
    private val PLACEHOLDERS: Map<String, String>
        get() = mapOf(
            "{PLAYER}" to playerHolder.player.name
        )

    override fun onInventoryCreated() {
        render()
        menuModel.updateInterval?.let(::startAutoUpdate)
    }

    private fun startAutoUpdate(interval: Long) {
        menuScope.launch(dispatchers.IO) {
            while (isActive) {
                delay(interval)
                withContext(dispatchers.BukkitMain) {
                    render()
                }
            }
        }
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onInventoryClosed(it: InventoryCloseEvent) = Unit

    private fun MenuModel.MenuItem.toItemStack(): ItemStack {
        val menuItem = this
        val material = Material.getMaterial(menuItem.material) ?: error("No material found named ${menuItem.material}")
        return ItemStack(material, menuItem.amount).apply {
            this.lore(menuItem.lore.map(kyoriComponentSerializer::toComponent))
            this.itemMeta = itemMeta.apply {
                this.displayName(menuItem.name.let(kyoriComponentSerializer::toComponent))
                this.setCustomModelData(menuItem.customModelData)
            }
        }
    }

    private fun isMeetConditions(conditions: List<MenuModel.Condition>): Boolean {
        if (conditions.isEmpty()) return true
        return conditions.any {
            when (it) {
                is MenuModel.Condition.Permission -> {
                    val hasPermission = playerHolder.player.hasPermission(it.permission)
                    if (it.isInverted) {
                        !hasPermission
                    } else {
                        hasPermission
                    }
                }
            }
        }
    }

    private fun isMeetClickConditions(menuItem: MenuModel.MenuItem): Boolean {
        return isMeetConditions(menuItem.clickableConditions)
    }

    private fun isMeetVisibilityConditions(menuItem: MenuModel.MenuItem): Boolean {
        return isMeetConditions(menuItem.visibilityConditions)
    }

    private fun processReward(menuItem: MenuModel.MenuItem) {
        when (val reward = menuItem.reward) {
            is MenuModel.Reward.ConsoleCommands -> {
                val consoleSender = Bukkit.getConsoleSender()
                val server = Bukkit.getServer()
                reward.commands.forEach { cmd ->
                    var command = cmd
                    PLACEHOLDERS.forEach { (k, v) ->
                        command = command.replace(k, v)
                    }
                    server.dispatchCommand(consoleSender, command)
                }
            }

            is MenuModel.Reward.PlayerCommands -> {
                val sender = playerHolder.player
                reward.commands.forEach { cmd ->
                    var command = cmd
                    PLACEHOLDERS.forEach { (k, v) ->
                        command = command.replace(k, v)
                    }
                    sender.performCommand(command)
                }
            }

            MenuModel.Reward.Nothing -> Unit
        }
    }

    private suspend fun isMeetPriceCheck(menuItem: MenuModel.MenuItem): Boolean {
        return when (val price = menuItem.price) {
            is MenuModel.Price.Money -> {
                economyProvider?.takeMoney(
                    playerHolder.player.uniqueId,
                    price.amount
                ) ?: false
            }

            MenuModel.Price.Nothing -> true
        }
    }

    @Suppress("CyclomaticComplexMethod") // todo
    override fun render() {
        super.render()
        menuModel.items.values.filter(::isMeetVisibilityConditions).forEach { menuItem ->
            InventorySlot.Builder()
                .setItemStack(menuItem.toItemStack())
                .setIndex(menuItem.index)
                .setOnClickListener {
                    val permission = menuItem.permission?.let(PluginPermission::CustomPermission)

                    val hasPermission = permission?.let(playerHolder.player.toPermissible()::hasPermission) ?: true
                    if (!hasPermission) {
                        translation.general.noPermission
                            .let(kyoriComponentSerializer::toComponent)
                            .run(playerHolder.player::sendMessage)
                        return@setOnClickListener
                    }

                    if (!isMeetClickConditions(menuItem)) return@setOnClickListener
                    menuScope.launch {
                        if (!isMeetPriceCheck(menuItem)) {
                            translation.general.notEnoughMoney
                                .let(kyoriComponentSerializer::toComponent)
                                .run(playerHolder.player::sendMessage)
                            return@launch
                        }

                        processReward(menuItem)
                    }
                }.build().setInventorySlot()
        }
    }
}
