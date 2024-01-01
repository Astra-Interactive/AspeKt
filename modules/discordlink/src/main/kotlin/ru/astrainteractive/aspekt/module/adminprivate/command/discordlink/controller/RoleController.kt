package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller

import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import ru.astrainteractive.aspekt.plugin.PluginConfiguration

internal interface RoleController {

    val configuration: PluginConfiguration.DiscordSRVLink
    suspend fun onLinked(e: AccountLinkedEvent)
    suspend fun onUnLinked(e: AccountUnlinkedEvent)
}
