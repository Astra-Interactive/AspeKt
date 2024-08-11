package ru.astrainteractive.aspekt

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.impl.RootModuleImpl
import ru.astrainteractive.aspekt.event.EventHandler
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Initial class for your plugin
 */
class AspeKt : JavaPlugin() {
    private val rootModule = RootModuleImpl()
    private val eventsModule: EventsModule
        get() = rootModule.eventsModule
    private val lifecycles: List<Lifecycle>
        get() = listOfNotNull(
            rootModule.autoBroadcastModule.lifecycle,
            rootModule.commandManagerModule,
            rootModule.coreModule,
            rootModule.menuModule.lifecycle,
            rootModule.discordLinkModule.lifecycle,
            rootModule.adminPrivateModule.lifecycle,
            rootModule.eventsModule.sitModule,
            rootModule.townyDiscordModule.lifecycle,
            rootModule.moneyDropModule.lifecycle,
            rootModule.autoCropModule.lifecycle,
            rootModule.newBeeModule.lifecycle,
            rootModule.antiSwearModule.lifecycle,
            rootModule.moneyAdvancementModule.lifecycle,
            rootModule.chatGameModule.lifecycle
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
