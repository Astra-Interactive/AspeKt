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
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.GlobalEventListener
import ru.astrainteractive.astralibs.menu.event.GlobalInventoryClickEvent
import ru.astrainteractive.klibs.kdi.getValue

/**
 * Initial class for your plugin
 */
class AspeKt : JavaPlugin() {
    private val rootModule by RootModuleImpl
    private val eventsModule: EventsModule by rootModule.eventsModule
    private val commandsModule: CommandsModule by rootModule.commandsModule
    private val controllersModule: ControllersModule by rootModule.controllersModule

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        rootModule.plugin.initialize(this)
        EventHandler(eventsModule)
        CommandManager(commandsModule)
        GlobalInventoryClickEvent.onEnable(this)
        GlobalEventListener.onEnable(this)
        rootModule.autoBroadcastJob.value.onEnable()
        rootModule.discordEvent.value?.onEnable()
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        controllersModule.sitController.onDisable()
        rootModule.autoBroadcastJob.value.onDisable()
        HandlerList.unregisterAll(this)
        GlobalEventListener.onDisable()
        PluginScope.close()
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
        rootModule.controllersModule.adminPrivateController.updateChunks()
        rootModule.autoBroadcastJob.value.apply {
            this.onDisable()
            this.onEnable()
        }
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
    }
}
