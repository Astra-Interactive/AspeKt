@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.api.Subscribe
import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import kotlinx.coroutines.launch
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di.DiscordEventDependencies
import ru.astrainteractive.aspekt.util.Lifecycle

/**
 * Template event class
 */
class DiscordEvent(module: DiscordEventDependencies) : DiscordEventDependencies by module, Lifecycle {
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

    override fun onEnable() {
        DiscordSRV.api.subscribe(this)
    }

    override fun onDisable() {
        DiscordSRV.api.unsubscribe(this)
    }
}
