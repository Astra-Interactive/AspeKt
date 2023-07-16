@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.event.discord

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.api.Subscribe
import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.event.discord.di.DiscordEventModule
import ru.astrainteractive.astralibs.async.PluginScope

/**
 * Template event class
 */
class DiscordEvent(module: DiscordEventModule) : DiscordEventModule by module {
    @Subscribe
    fun onAccountLinked(e: AccountLinkedEvent) {
        PluginScope.launch(Dispatchers.IO) {
            discordController.onLinked(e)
            luckPermsController.onLinked(e)
        }
    }

    @Subscribe
    fun onAccountUnlinked(e: AccountUnlinkedEvent) {
        PluginScope.launch(Dispatchers.IO) {
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
