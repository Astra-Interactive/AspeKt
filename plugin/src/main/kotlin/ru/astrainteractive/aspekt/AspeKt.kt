@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt

import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.commands.CommandManager
import ru.astrainteractive.aspekt.commands.di.CommandsModule
import ru.astrainteractive.aspekt.di.ControllersModule
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.di.impl.CommandsModuleImpl
import ru.astrainteractive.aspekt.di.impl.ControllersModuleImpl
import ru.astrainteractive.aspekt.di.impl.EventsModuleImpl
import ru.astrainteractive.aspekt.di.impl.RootModuleImpl
import ru.astrainteractive.aspekt.events.EventHandler
import ru.astrainteractive.aspekt.events.di.EventsModule
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.GlobalEventListener
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.menu.event.GlobalInventoryClickEvent

/**
 * Initial class for your plugin
 */
class AspeKt : JavaPlugin() {
    init {
        RootModuleImpl.plugin.initialize(this)
    }

    private val rootModule: RootModule by RootModuleImpl
    private val eventsModule: EventsModule by EventsModuleImpl
    private val commandsModule: CommandsModule by CommandsModuleImpl
    private val controllersModule: ControllersModule by ControllersModuleImpl

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        EventHandler(eventsModule)
        CommandManager(commandsModule, this)
        GlobalInventoryClickEvent.onEnable(this)
        GlobalEventListener.onEnable(this)
        rootModule.autoBroadcastJob.value.onEnable()
        rootModule.discordEvent.value?.onEnable()
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        controllersModule.sitController.value.onDisable()
        rootModule.autoBroadcastJob.value.onDisable()
        HandlerList.unregisterAll(this)
        GlobalEventListener.onDisable()
        PluginScope.close()
        rootModule.discordEvent.value?.onDisable()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        controllersModule.sitController.value.onDisable()
        RootModuleImpl.configFileManager.value.reload()
        RootModuleImpl.pluginConfig.reload()
        RootModuleImpl.translation.reload()
        rootModule.autoBroadcastJob.value.apply {
            this.onDisable()
            this.onEnable()
        }
    }
}
