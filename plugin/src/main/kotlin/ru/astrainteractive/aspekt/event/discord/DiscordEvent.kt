@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.event.discord

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.api.Subscribe
import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import kotlinx.coroutines.launch
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.event.discord.di.DiscordEventDependencies

/**
 * Template event class
 */
class DiscordEvent(module: DiscordEventDependencies) : DiscordEventDependencies by module {
    @Subscribe
    fun onAccountLinked(e: AccountLinkedEvent) {
        scope.launch(dispatchers.IO) {
            discordController.onLinked(e)
            luckPermsController.onLinked(e)
        }
    }

    @Subscribe
    fun onAccountUnlinked(e: AccountUnlinkedEvent) {
        scope.launch(dispatchers.IO) {
            discordController.onUnLinked(e)
            luckPermsController.onUnLinked(e)
        }
    }

    /**
     * TODO
     */
//    fun onPlayerLeaveDiscord() {
//        PluginScope.launch(Dispatchers.IO) {
//            discordController.onUnLinked(e)
//            luckPermsController.onUnLinked(e)
//        }
//    }

    fun onEnable() {
        DiscordSRV.api.subscribe(this)
    }

    fun onDisable() {
        DiscordSRV.api.unsubscribe(this)
    }
}
