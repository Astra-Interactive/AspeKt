@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.events.discord

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.api.Subscribe
import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.events.discord.controllers.RoleController
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.getValue

/**
 * Template event class
 */
class DiscordEvent(
    discordController: Dependency<RoleController>,
    luckPermsController: Dependency<RoleController>
) {
    private val discordController by discordController
    private val luckPermsController by luckPermsController

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
