@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.command.CommandManager
import ru.astrainteractive.aspekt.command.di.CommandsModule
import ru.astrainteractive.aspekt.di.ControllersModule
import ru.astrainteractive.aspekt.di.impl.RootModuleImpl
import ru.astrainteractive.aspekt.event.EventHandler
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.klibs.kdi.getValue

/**
 * Initial class for your plugin
 */
class AspeKt : JavaPlugin() {
    private val rootModule = RootModuleImpl()
    private val eventsModule: EventsModule by rootModule.eventsModule
    private val commandsModule: CommandsModule by rootModule.commandsModule
    private val controllersModule: ControllersModule by rootModule.controllersModule

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        rootModule.plugin.initialize(this)
        EventHandler(eventsModule)
        CommandManager(commandsModule, rootModule.translationContext)
        rootModule.inventoryClickEventListener.value.onEnable(this)
        rootModule.eventListener.value.onEnable(this)
        rootModule.autoBroadcastJob.value.onEnable()
        rootModule.discordEvent.value?.onEnable()
        rootModule.economyProvider.reload()
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        controllersModule.sitController.onDisable()
        rootModule.autoBroadcastJob.value.onDisable()
        HandlerList.unregisterAll(this)
        rootModule.inventoryClickEventListener.value.onDisable()
        rootModule.eventListener.value.onDisable()
        rootModule.scope.value.close()
        rootModule.discordEvent.value?.onDisable()
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        controllersModule.sitController.onDisable()
        rootModule.configFileManager.value.reload()
        rootModule.pluginConfig.reload()
        rootModule.translation.reload()
        rootModule.menuModels.reload()
        rootModule.economyProvider.reload()
        rootModule.controllersModule.adminPrivateController.updateChunks()
        rootModule.tempFileManager.reload()
        rootModule.autoBroadcastJob.value.apply {
            this.onDisable()
            this.onEnable()
        }
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
    }
}
