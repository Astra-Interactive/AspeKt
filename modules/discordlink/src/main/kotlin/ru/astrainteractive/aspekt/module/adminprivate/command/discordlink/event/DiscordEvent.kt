@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.event

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.api.Subscribe
import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di.DiscordEventDependencies
import ru.astrainteractive.aspekt.util.Lifecycle
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

/**
 * Template event class
 */
internal class DiscordEvent(module: DiscordEventDependencies) : DiscordEventDependencies by module, Lifecycle {
    private val controllers by Provider {
        listOf(discordController, luckPermsController, addMoneyController)
    }

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

    private val rawJdaListener = object : ListenerAdapter() {
        override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
            scope.launch(dispatchers.IO) {
                val player = Bukkit.getOfflinePlayer(DiscordSRV.getPlugin().accountLinkManager.getUuid(event.user.id))
                controllers.forEach { it.onUnLinked(player, event.user) }
            }
        }
    }

    override fun onEnable() {
        DiscordSRV.api.subscribe(this)
        DiscordSRV.getPlugin().jda.addEventListener(rawJdaListener)
    }

    override fun onDisable() {
        DiscordSRV.api.unsubscribe(this)
        DiscordSRV.getPlugin().jda.removeEventListener(rawJdaListener)
    }
}
