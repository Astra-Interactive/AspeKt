package ru.astrainteractive.aspekt.gui.menu

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.plugin.MenuModel
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.menu.clicker.ClickListener
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.utils.ItemStackButtonBuilder
import ru.astrainteractive.astralibs.utils.convertHex
import ru.astrainteractive.astralibs.utils.hex
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class MenuGui(
    player: Player,
    private val economyProvider: EconomyProvider?,
    private val translation: PluginTranslation,
    private val menuModel: MenuModel
) : Menu() {
    override val menuSize: MenuSize = menuModel.size
    override var menuTitle: String = menuModel.title.hex()
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

    @Suppress("CyclomaticComplexMethod") // todo
    private fun render() {
        clickListener.clearClickListener()
        menuModel.items.values.filter(::isMeetVisibilityConditions).forEach { menuItem ->
            ItemStackButtonBuilder {
                this.itemStack = menuItem.toItemStack()
                this.index = menuItem.index
                this.onClick = onClick@{
                    val permission = menuItem.permission?.let(PluginPermission::CustomPermission)
                    val hasPermission = permission?.hasPermission(playerHolder.player) ?: true
                    if (!hasPermission) {
                        playerHolder.player.sendMessage(translation.noPermission)
                        return@onClick
                    }

                    if (!isMeetClickConditions(menuItem)) return@onClick
                    val hasPriceCheckPassed = when (menuItem.price) {
                        is MenuModel.Price.Money -> {
                            economyProvider?.takeMoney(
                                playerHolder.player.uniqueId,
                                menuItem.price.amount
                            ) ?: false
                        }

                        MenuModel.Price.Nothing -> true
                    }
                    if (!hasPriceCheckPassed) {
                        playerHolder.player.sendMessage(translation.notEnoughMoney)
                        return@onClick
                    }

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
                    render()
                }
            }.also(clickListener::remember).setInventoryButton()
        }
    }
}
