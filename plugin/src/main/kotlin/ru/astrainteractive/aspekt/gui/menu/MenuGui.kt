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

class MenuGui(
    player: Player,
    private val economyProvider: EconomyProvider,
    private val translation: PluginTranslation,
    private val menuModel: MenuModel
) : Menu() {
    override val menuSize: MenuSize = menuModel.size
    override var menuTitle: String = menuModel.title
    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)
    private val clickListener: ClickListener = MenuClickListener()
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
            this.lore = menuItem.lore
            this.itemMeta = itemMeta.apply {
                this.setDisplayName(menuItem.name)
                this.setCustomModelData(menuItem.customModelData)
            }
        }
    }

    private fun render() {
        clickListener.clearClickListener()
        menuModel.items.values.forEach { menuItem ->
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
                    val hasPriceCheckPassed = when (menuItem.price) {
                        is MenuModel.Price.Money -> {
                            economyProvider.takeMoney(playerHolder.player.uniqueId, menuItem.price.amount.toDouble())
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
                            menuItem.reward.commands.forEach { command ->
                                server.dispatchCommand(consoleSender, command)
                            }
                        }

                        is MenuModel.Reward.PlayerCommands -> {
                            val sender = playerHolder.player
                            menuItem.reward.commands.forEach { command ->
                                sender.performCommand(command)
                            }
                        }

                        MenuModel.Reward.Nothing -> Unit
                    }
                }
            }.also(clickListener::remember).setInventoryButton()
        }
    }
}
