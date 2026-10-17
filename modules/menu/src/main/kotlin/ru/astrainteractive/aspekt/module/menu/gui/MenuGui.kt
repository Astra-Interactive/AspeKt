package ru.astrainteractive.aspekt.module.menu.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.module.menu.model.MenuConfiguration
import ru.astrainteractive.aspekt.plugin.model.PluginNamedPermission
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astralibs.localization.markup.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.core.setInventorySlot
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.api.InventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astralibs.server.permission.asKPermissible
import ru.astrainteractive.astralibs.server.util.asKAudience
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Suppress("TooManyFunctions")
internal class MenuGui(
    player: Player,
    private val economyProvider: EconomyFacade?,
    private val translation: PluginTranslation,
    private val menuModel: MenuConfiguration,
    private val dispatchers: KotlinDispatchers,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) : InventoryMenu() {
    override val menuScope: CoroutineScope = CoroutineFeature.Default(dispatchers.Main).withTimings()
    override val childComponents: List<CoroutineScope> = emptyList()
    override val inventorySize: InventorySize = menuModel.size
    override val title: Component = kyoriComponentSerializer.toComponent(menuModel.title)
    private val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    @Suppress("VariableNaming")
    private val PLACEHOLDERS: Map<String, String>
        get() = mapOf(
            "{PLAYER}" to playerHolder.player.name
        )

    override fun onInventoryOpenEvent(e: InventoryOpenEvent) {
        render()
        menuModel.updateInterval?.milliseconds?.let(::startAutoUpdate)
    }

    private fun startAutoUpdate(interval: Duration) {
        menuScope.launch(dispatchers.IO) {
            while (isActive) {
                delay(interval)
                withContext(dispatchers.Main) {
                    render()
                }
            }
        }
    }

    override fun onInventoryClickEvent(e: InventoryClickEvent) {
        super.onInventoryClickEvent(e)
        e.isCancelled = true
    }

    private fun MenuConfiguration.MenuItem.toItemStack(): ItemStack {
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

    private fun isMeetConditions(conditions: List<MenuConfiguration.Condition>): Boolean {
        if (conditions.isEmpty()) return true
        return conditions.any {
            when (it) {
                is MenuConfiguration.Condition.Permission -> {
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

    private fun isMeetClickConditions(menuItem: MenuConfiguration.MenuItem): Boolean {
        return isMeetConditions(menuItem.clickableConditions)
    }

    private fun isMeetVisibilityConditions(menuItem: MenuConfiguration.MenuItem): Boolean {
        return isMeetConditions(menuItem.visibilityConditions)
    }

    private fun processReward(menuItem: MenuConfiguration.MenuItem) {
        when (val reward = menuItem.reward) {
            is MenuConfiguration.Reward.ConsoleCommands -> {
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

            is MenuConfiguration.Reward.PlayerCommands -> {
                val sender = playerHolder.player
                reward.commands.forEach { cmd ->
                    var command = cmd
                    PLACEHOLDERS.forEach { (k, v) ->
                        command = command.replace(k, v)
                    }
                    sender.performCommand(command)
                }
            }

            MenuConfiguration.Reward.Nothing -> Unit
        }
    }

    private suspend fun isMeetPriceCheck(menuItem: MenuConfiguration.MenuItem): Boolean {
        return when (val price = menuItem.price) {
            is MenuConfiguration.Price.Money -> {
                economyProvider?.takeMoney(
                    playerHolder.player.uniqueId,
                    price.amount
                ) ?: false
            }

            MenuConfiguration.Price.Nothing -> true
        }
    }

    @Suppress("CyclomaticComplexMethod") // todo
    override fun render() {
        super.render()
        menuModel.items.values.filter(::isMeetVisibilityConditions).forEach { menuItem ->
            setInventorySlot(
                InventorySlot.Builder()
                    .setItemStack(menuItem.toItemStack())
                    .setIndex(menuItem.index)
                    .setOnClickListener {
                        val permission = menuItem.permission?.let(::PluginNamedPermission)

                        val hasPermission = permission?.let(playerHolder.player.asKPermissible()::hasPermission) ?: true
                        if (!hasPermission) {
                            playerHolder.player.asKAudience().sendMessage(translation.commandError.noPermission)
                            return@setOnClickListener
                        }

                        if (!isMeetClickConditions(menuItem)) return@setOnClickListener
                        menuScope.launch {
                            if (!isMeetPriceCheck(menuItem)) {
                                playerHolder.player.asKAudience().sendMessage(translation.menu.notEnoughMoney)
                                return@launch
                            }

                            processReward(menuItem)
                        }
                    }.build()
            )
        }
    }
}
