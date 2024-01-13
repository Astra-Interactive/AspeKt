package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller

import github.scarsz.discordsrv.dependencies.jda.api.JDA
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role
import github.scarsz.discordsrv.dependencies.jda.api.entities.User
import github.scarsz.discordsrv.util.DiscordUtil
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration

@Suppress("DuplicatedCode")
internal class DiscordController(
    module: RoleControllerDependencies,
) : RoleController,
    RoleControllerDependencies by module {

    private val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink

    private suspend fun JDA.mapRoles(list: List<String>): Set<Role> = list.mapNotNull(::getRoleById).toSet()

    private suspend fun changeRoles(
        rolesToAdd: List<String>,
        rolesToRemove: List<String>,
        player: OfflinePlayer,
        discordUser: User
    ) {
        val member = discordUser.id.let(DiscordUtil::getMemberById) ?: run {
            logger.info("DiscordEvent", "Игрок ${player.name} не на нашем сервере")
            return
        }
        discordUser.jda.mapRoles(rolesToAdd).let { roles ->
            DiscordUtil.addRolesToMember(member, roles)
            logger.info("DiscordEvent", "Игроку ${player.name} выданы роли ${roles.map { it.id to it.name }}")
        }
        discordUser.jda.mapRoles(rolesToRemove).let { roles ->
            DiscordUtil.removeRolesFromMember(member, roles)
            logger.info("DiscordEvent", "У игрока ${player.name} сняты роли ${roles.map { it.id to it.name }}")
        }
    }

    override suspend fun onLinked(player: OfflinePlayer, discordUser: User) {
        logger.info("DiscordEvent", "Игрок ${player.name} линкует аккаунт")
        changeRoles(
            rolesToAdd = configuration.onLinked.discord.addRoles,
            rolesToRemove = configuration.onLinked.discord.removeRoles,
            player = player,
            discordUser = discordUser
        )
    }

    override suspend fun onUnLinked(player: OfflinePlayer, discordUser: User) {
        logger.info("DiscordEvent", "Игрок ${player.name} отменил линк аккаунта")
        changeRoles(
            rolesToAdd = configuration.onUnlinked.discord.addRoles,
            rolesToRemove = configuration.onUnlinked.discord.removeRoles,
            player = player,
            discordUser = discordUser
        )
    }
}
