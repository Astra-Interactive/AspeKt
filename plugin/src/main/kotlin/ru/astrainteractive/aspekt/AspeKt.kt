@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt

import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.setupWithSpigot
import ru.astrainteractive.aspekt.commands.CommandManager
import ru.astrainteractive.aspekt.events.EventHandler
import ru.astrainteractive.aspekt.modules.*
import ru.astrainteractive.astralibs.di.Singleton
import ru.astrainteractive.astralibs.events.GlobalEventListener
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.menu.event.GlobalInventoryClickEvent

/**
 * Initial class for your plugin
 */
class AspeKt : JavaPlugin() {
    companion object : Singleton<AspeKt>()

    init {
        instance = this
    }

    private val discordEvent by ServiceLocator.discordEventModule
    private val autoBroadcast by ServiceLocator.autoBroadcastModule
    private val sitController by ServiceLocator.Controllers.sitControllerModule

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        Logger.setupWithSpigot("AspeKt", this)
        EventHandler(
            sitControllerDependency = ServiceLocator.Controllers.sitControllerModule,
            sortControllerDependency = ServiceLocator.Controllers.sortControllerModule,
            pluginConfigDep = ServiceLocator.pluginConfigModule,
            bukkitDispatchers = ServiceLocator.bukkitDispatchers.value
        )
        CommandManager(
            serviceLocator = ServiceLocator,
            controllers = ServiceLocator.Controllers
        )
        GlobalInventoryClickEvent.onEnable(this)
        GlobalEventListener.onEnable(this)
        autoBroadcast.onEnable()
        discordEvent?.onEnable()
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        sitController.onDisable()
        autoBroadcast.onDisable()
        HandlerList.unregisterAll(this)
        GlobalEventListener.onDisable()
        PluginScope.close()
        discordEvent?.onDisable()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        sitController.onDisable()
        ServiceLocator.configFileManager.value.reload()
        ServiceLocator.pluginConfigModule.reload()
        ServiceLocator.TranslationModule.reload()

        autoBroadcast.onDisable()
        autoBroadcast.onEnable()
    }

}


