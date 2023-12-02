package ru.astrainteractive.aspekt.gui.menu

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
import ru.astrainteractive.aspekt.plugin.MenuModel
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.clicker.ClickListener
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.util.convertHex
import ru.astrainteractive.astralibs.util.hex
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

@Suppress("TooManyFunctions")
class MenuGui(
    player: Player,
    private val economyProvider: EconomyProvider?,
    private val translation: PluginTranslation,
    private val menuModel: MenuModel,
    private val dispatchers: BukkitDispatchers,
    translationContext: BukkitTranslationContext
) : Menu(),
    BukkitTranslationContext by translationContext {
    override val menuSize: MenuSize = menuModel.size
    override var menuTitle: Component = StringDesc.Raw(menuModel.title).toComponent()
    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)
    private val clickListener: ClickListener = MenuClickListener()

    @Suppress("VariableNaming")
    private val PLACEHOLDERS by Provider {
        mapOf(
            "{PLAYER}" to playerHolder.player.name
        )
    }

    override fun onCreated() {
        render()
        menuModel.updateInterval?.let(::startAutoUpdate)
    }

    private fun startAutoUpdate(interval: Long) {
        componentScope.launch(dispatchers.IO) {
            while (isActive) {
                delay(interval)
                withContext(dispatchers.BukkitMain) {
                    render()
                }
            }
        }
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        clickListener.onClick(e)
        e.isCancelled = true
    }

    override fun onInventoryClose(it: InventoryCloseEvent) = Unit

    private fun MenuModel.MenuItem.toItemStack(): ItemStack {
        val menuItem = this
        val material = Material.getMaterial(menuItem.material) ?: error("No material found named ${menuItem.material}")
        return ItemStack(material, menuItem.amount).apply {
            this.lore = menuItem.lore.map(::convertHex)
            this.itemMeta = itemMeta.apply {
                this.setDisplayName(menuItem.name.hex())
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
        when (menuItem.reward) {
            is MenuModel.Reward.ConsoleCommands -> {
                val consoleSender = Bukkit.getConsoleSender()
                val server = Bukkit.getServer()
                menuItem.reward.commands.forEach { cmd ->
                    var command = cmd
                    PLACEHOLDERS.forEach { (k, v) ->
                        command = command.replace(k, v)
                    }
                    server.dispatchCommand(consoleSender, command)
                }
            }

            is MenuModel.Reward.PlayerCommands -> {
                val sender = playerHolder.player
                menuItem.reward.commands.forEach { cmd ->
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

    private fun isMeetPriceCheck(menuItem: MenuModel.MenuItem): Boolean {
        return when (menuItem.price) {
            is MenuModel.Price.Money -> {
                economyProvider?.takeMoney(
                    playerHolder.player.uniqueId,
                    menuItem.price.amount
                ) ?: false
            }

            MenuModel.Price.Nothing -> true
        }
    }

    @Suppress("CyclomaticComplexMethod") // todo
    private fun render() {
        clickListener.clearClickListener()
        menuModel.items.values.filter(::isMeetVisibilityConditions).forEach { menuItem ->
            InventorySlot.Builder {
                this.itemStack = menuItem.toItemStack()
                this.index = menuItem.index
                this.click = Click {
                    val permission = menuItem.permission?.let(PluginPermission::CustomPermission)

                    val hasPermission = permission?.let(playerHolder.player.toPermissible()::hasPermission) ?: true
                    if (!hasPermission) {
                        playerHolder.player.sendMessage(translation.general.noPermission)
                        return@Click
                    }

                    if (!isMeetClickConditions(menuItem)) return@Click
                    if (!isMeetPriceCheck(menuItem)) {
                        playerHolder.player.sendMessage(translation.general.notEnoughMoney)
                        return@Click
                    }

                    processReward(menuItem)
                }
            }.also(clickListener::remember).setInventorySlot()
        }
    }
}
