package ru.astrainteractive.astraessentials

import kotlinx.coroutines.cancel
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.GlobalEventManager
import ru.astrainteractive.astralibs.menu.SharedInventoryClickEvent
import ru.astrainteractive.astralibs.utils.setupWithSpigot
import ru.astrainteractive.astraessentials.commands.CommandManager
import ru.astrainteractive.astraessentials.events.EventHandler
import ru.astrainteractive.astraessentials.events.sit.SitController
import ru.astrainteractive.astraessentials.modules.*
import ru.astrainteractive.astraessentials.utils.Files
import ru.astrainteractive.astraessentials.utils.Singleton

/**
 * Initial class for your plugin
 */
class AstraEssentials : JavaPlugin() {
    companion object : Singleton<AstraEssentials>()

    init {
        instance = this
    }

    private val discordEvent by discordEventModule


    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        AstraLibs.rememberPlugin(this)
        Logger.setupWithSpigot("AstraTemplate")
        EventHandler()
        CommandManager()
        SharedInventoryClickEvent.onEnable(GlobalEventManager)
        AutoBroadcast.onEnable()
        discordEvent?.onEnable()
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        SitController.onDisable()
        AutoBroadcast.onDisable()
        HandlerList.unregisterAll(this)
        GlobalEventManager.onDisable()
        PluginScope.cancel()
        discordEvent?.onDisable()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        SitController.onDisable()
        Files.configFile.reload()
        PluginConfigModule.reload()
        TranslationModule.reload()

        AutoBroadcast.onDisable()
        AutoBroadcast.onEnable()
    }

}


