package ru.astrainteractive.astraessentials.events.discord.controllers

import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import github.scarsz.discordsrv.dependencies.jda.api.JDA
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role
import github.scarsz.discordsrv.util.DiscordUtil
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astraessentials.modules.PluginConfigModule
import ru.astrainteractive.astraessentials.utils.PluginConfiguration

class DiscordController: RoleController {
    private val pluginConfiguration by PluginConfigModule
    override val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink
    private suspend fun mapRoles(jda: JDA, list: List<String>): List<Role> =
        list.mapNotNull { jda.getRoleById(it) }
    override suspend fun onLinked(e: AccountLinkedEvent) {
        Logger.log("DiscordEvent", "Игрок ${e.player.name} линкует аккаунт")
        val member = DiscordUtil.getMemberById(e.user.id) ?: run {
            Logger.log("DiscordEvent", "Игрок ${e.player.name} не на нашем сервере")
            return
        }
        mapRoles(e.user.jda,configuration.discordOnLinked.addRoles.value).forEach {
            DiscordUtil.addRoleToMember(member, it)
            Logger.log("DiscordEvent", "Игроку ${e.player.name} выдана роль ${it.id}: ${it.name}")
        }

        mapRoles(e.user.jda,configuration.discordOnLinked.removeRoles.value).forEach {
            DiscordUtil.removeRolesFromMember(member, it)
            Logger.log("DiscordEvent", "У игрока ${e.player.name} снята роль ${it.id}: ${it.name}")
        }
    }

    override suspend fun onUnLinked(e: AccountUnlinkedEvent) {
        Logger.log("DiscordEvent", "Игрок ${e.player.name} отменил линк аккаунта")
        val member = DiscordUtil.getMemberById(e.discordUser.id) ?: run {
            Logger.log("DiscordEvent", "Игрок ${e.player.name} не на нашем сервере")
            return
        }
        mapRoles(e.discordUser.jda,configuration.discordOnUnLinked.addRoles.value).forEach {
            DiscordUtil.addRoleToMember(member, it)
            Logger.log("DiscordEvent", "Игроку ${e.player.name} выдана роль ${it.id}: ${it.name}")
        }

        mapRoles(e.discordUser.jda,configuration.discordOnUnLinked.removeRoles.value).forEach {
            DiscordUtil.removeRolesFromMember(member, it)
            Logger.log("DiscordEvent", "У игрока ${e.player.name} снята роль ${it.id}: ${it.name}")
        }
    }

}