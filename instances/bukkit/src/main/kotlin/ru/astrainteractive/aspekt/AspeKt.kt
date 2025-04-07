package ru.astrainteractive.aspekt

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

/**
 * Initial class for your plugin
 */
class AspeKt : LifecyclePlugin(), Logger by JUtiltLogger("AspeKt") {
    private val rootModule = RootModule(this)
    private val lifecycles: List<Lifecycle>
        get() = listOfNotNull(
            rootModule.economyModule.lifecycle,
            rootModule.autoBroadcastModule.lifecycle,
            rootModule.sitModule.lifecycle,
            rootModule.commandManagerModule,
            rootModule.coreModule.lifecycle,
            rootModule.menuModule.lifecycle,
            rootModule.discordLinkModule.lifecycle,
            rootModule.adminPrivateModule.lifecycle,
            rootModule.townyDiscordModule.lifecycle,
            rootModule.moneyDropModule.lifecycle,
            rootModule.autoCropModule.lifecycle,
            rootModule.newBeeModule.lifecycle,
            rootModule.antiSwearModule.lifecycle,
            rootModule.moneyAdvancementModule.lifecycle,
            rootModule.chatGameModule.lifecycle,
            rootModule.entitiesModule.lifecycle,
            rootModule.treeCapitatorModule.lifecycle,
            rootModule.restrictionModule.lifecycle,
            rootModule.inventorySortModule.lifecycle,
            rootModule.jailModule.lifecycle,
            rootModule.invisibleItemFrameModule.lifecycle
        )

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
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

    override fun onReload() {
        lifecycles.forEach(Lifecycle::onReload)
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
    }
}
