package ru.astrainteractive.aspekt.module.claims.command.discordlink.event

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.api.Subscribe
import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import kotlinx.coroutines.launch
import org.bukkit.plugin.Plugin
import ru.astrainteractive.aspekt.module.claims.command.discordlink.di.DiscordEventDependencies
import ru.astrainteractive.astralibs.event.EventListener

/**
 * Template event class
 */
internal class DiscordEvent(
    dependencies: DiscordEventDependencies
) : DiscordEventDependencies by dependencies,
    EventListener {
    private val controllers get() = listOf(discordController, luckPermsController, addMoneyController)

    @Subscribe
    fun onAccountLinked(e: AccountLinkedEvent) {
        scope.launch(dispatchers.IO) {
            controllers.forEach { it.onLinked(e.player, e.user) }
        }
    }

    @Subscribe
    fun onAccountUnlinked(e: AccountUnlinkedEvent) {
        scope.launch(dispatchers.IO) {
            controllers.forEach { it.onUnLinked(e.player, e.discordUser) }
        }
    }

    override fun onEnable(plugin: Plugin) {
        DiscordSRV.api.subscribe(this)
    }

    override fun onDisable() {
        DiscordSRV.api.unsubscribe(this)
    }
}
