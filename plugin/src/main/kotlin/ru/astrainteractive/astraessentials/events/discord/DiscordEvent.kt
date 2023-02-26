package ru.astrainteractive.astraessentials.events.discord

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.api.Subscribe
import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astraessentials.events.discord.controllers.DiscordController
import ru.astrainteractive.astraessentials.events.discord.controllers.LuckPermsController
import ru.astrainteractive.astraessentials.events.discord.controllers.RoleController
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue


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