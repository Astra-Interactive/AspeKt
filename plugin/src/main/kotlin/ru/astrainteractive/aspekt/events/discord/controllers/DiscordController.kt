package ru.astrainteractive.aspekt.events.discord.controllers

import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import github.scarsz.discordsrv.dependencies.jda.api.JDA
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role
import github.scarsz.discordsrv.util.DiscordUtil
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.logging.Logger

@Suppress("DuplicatedCode")
class DiscordController(
    pluginConfiguration: Dependency<PluginConfiguration>,
    logger: Dependency<Logger>
) : RoleController {
    private val logger by logger
    private val pluginConfiguration by pluginConfiguration

    override val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink

    private suspend fun mapRoles(jda: JDA, list: List<String>): List<Role> =
        list.mapNotNull { jda.getRoleById(it) }

    override suspend fun onLinked(e: AccountLinkedEvent) {
        logger.info("DiscordEvent", "Игрок ${e.player.name} линкует аккаунт")
        val member = e.user?.id?.let(DiscordUtil::getMemberById) ?: run {
            logger.info("DiscordEvent", "Игрок ${e.player.name} не на нашем сервере")
            return
        }
        mapRoles(e.user.jda, configuration.discordOnLinked.addRoles.value).forEach {
            DiscordUtil.addRoleToMember(member, it)
            logger.info("DiscordEvent", "Игроку ${e.player.name} выдана роль ${it.id}: ${it.name}")
        }

        mapRoles(e.user.jda, configuration.discordOnLinked.removeRoles.value).forEach {
            DiscordUtil.removeRolesFromMember(member, it)
            logger.info("DiscordEvent", "У игрока ${e.player.name} снята роль ${it.id}: ${it.name}")
        }
    }

    override suspend fun onUnLinked(e: AccountUnlinkedEvent) {
        logger.info("DiscordEvent", "Игрок ${e.player.name} отменил линк аккаунта")
        val member = e.discordUser?.id?.let(DiscordUtil::getMemberById) ?: run {
            logger.info("DiscordEvent", "Игрок ${e.player.name} не на нашем сервере")
            return
        }
        mapRoles(e.discordUser.jda, configuration.discordOnUnLinked.addRoles.value).forEach {
            DiscordUtil.addRoleToMember(member, it)
            logger.info("DiscordEvent", "Игроку ${e.player.name} выдана роль ${it.id}: ${it.name}")
        }

        mapRoles(e.discordUser.jda, configuration.discordOnUnLinked.removeRoles.value).forEach {
            DiscordUtil.removeRolesFromMember(member, it)
            logger.info("DiscordEvent", "У игрока ${e.player.name} снята роль ${it.id}: ${it.name}")
        }
    }
}
