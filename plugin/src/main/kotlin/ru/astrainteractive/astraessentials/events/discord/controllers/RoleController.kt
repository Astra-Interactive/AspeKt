package ru.astrainteractive.astraessentials.events.discord.controllers

import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import ru.astrainteractive.astraessentials.utils.PluginConfiguration

interface RoleController {

    val configuration: PluginConfiguration.DiscordSRVLink
    suspend fun onLinked(e: AccountLinkedEvent)
    suspend fun onUnLinked(e: AccountUnlinkedEvent)
}

