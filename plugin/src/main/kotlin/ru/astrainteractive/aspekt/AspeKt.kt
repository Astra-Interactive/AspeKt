@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.di.impl.RootModuleImpl
import ru.astrainteractive.aspekt.event.EventHandler
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.util.Lifecycle
import ru.astrainteractive.klibs.kdi.getValue

/**
 * Initial class for your plugin
 */
class AspeKt : JavaPlugin() {
    private val rootModule = RootModuleImpl()
    private val eventsModule: EventsModule by rootModule.eventsModule
    private val lifecycles: List<Lifecycle>
        get() = listOfNotNull(
            rootModule.autoBroadcastModule.autoBroadcastLifecycleFactory.create(),
            rootModule.commandManagerModule,
            rootModule.coreModule,
            rootModule.menuModule.menuModuleLifecycleFactory.create(),
            rootModule.discordLinkModule.discordLinkLifecycleFactory.create(),
            rootModule.adminPrivateModule.adminPrivateLifecycleFactory.create(),
            rootModule.eventsModule.sitModule,
            rootModule.townyDiscordModule.lifecycle
        )

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        rootModule.coreModule.plugin.initialize(this)
        EventHandler(eventsModule)
        lifecycles.forEach(Lifecycle::onEnable)
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        lifecycles.forEach(Lifecycle::onDisable)
        HandlerList.unregisterAll(this)
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        lifecycles.forEach(Lifecycle::onReload)
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
    }
}
