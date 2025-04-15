package ru.astrainteractive.aspekt.module.claims.command.discordlink.controller

import github.scarsz.discordsrv.dependencies.jda.api.JDA
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role
import github.scarsz.discordsrv.dependencies.jda.api.entities.User
import github.scarsz.discordsrv.util.DiscordUtil
import kotlinx.coroutines.awaitAll
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.claims.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.util.RestActionExt.asyncResult

@Suppress("DuplicatedCode")
internal class DiscordController(
    module: RoleControllerDependencies,
) : RoleController,
    RoleController.Discord,
    RoleControllerDependencies by module {
    private val logger = java.util.logging.Logger.getLogger("DiscordController")

    private val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink

    private suspend fun JDA.mapRoles(list: List<String>): Set<Role> = list.mapNotNull(::getRoleById).toSet()

    override suspend fun addRoleToMembers(
        memberIds: Set<String>,
        roleId: String,
        guild: Guild
    ) {
        val role = guild.getRoleById(roleId) ?: error("Role $roleId not found")
        val memberIdsWithNoRole = guild.retrieveMembersByIds(*memberIds.toTypedArray())
            .get()
            .filterNot { it.roles.contains(role) }
            .map(Member::getId)

        memberIdsWithNoRole
            .map { guild.addRoleToMember(it, role).asyncResult() }
            .awaitAll()
    }

    override suspend fun removeRoleFromMembersWithRole(
        whitelistedUserIds: Set<String>,
        roleId: String,
        guild: Guild
    ) {
        val role = guild.getRoleById(roleId) ?: error("Role $roleId not found")
        val memberIdsWithRole = guild.getMembersWithRoles(role)
            .map(Member::getId)
            .toSet()
            .minus(whitelistedUserIds)

        val nonWhitelistedRoles = memberIdsWithRole - whitelistedUserIds

        nonWhitelistedRoles
            .map { guild.removeRoleFromMember(it, role).asyncResult() }
            .awaitAll()
    }

    private suspend fun changeRoles(
        rolesToAdd: List<String>,
        rolesToRemove: List<String>,
        player: OfflinePlayer,
        discordUser: User
    ) {
        val member = discordUser.id.let(DiscordUtil::getMemberById) ?: run {
            logger.info("Игрок ${player.name} не на нашем сервере")
            return
        }
        discordUser.jda.mapRoles(rolesToAdd).let { roles ->
            DiscordUtil.addRolesToMember(member, roles)
            logger.info("Игроку ${player.name} выданы роли ${roles.map { it.id to it.name }}")
        }
        discordUser.jda.mapRoles(rolesToRemove).let { roles ->
            DiscordUtil.removeRolesFromMember(member, roles)
            logger.info("У игрока ${player.name} сняты роли ${roles.map { it.id to it.name }}")
        }
    }

    override suspend fun onLinked(player: OfflinePlayer, discordUser: User) {
        logger.info("Игрок ${player.name} линкует аккаунт")
        changeRoles(
            rolesToAdd = listOfNotNull(configuration.discordLinkedRole),
            rolesToRemove = emptyList(),
            player = player,
            discordUser = discordUser
        )
    }

    override suspend fun onUnLinked(player: OfflinePlayer, discordUser: User) {
        logger.info("Игрок ${player.name} отменил линк аккаунта")
        changeRoles(
            rolesToAdd = emptyList(),
            rolesToRemove = listOfNotNull(configuration.discordLinkedRole),
            player = player,
            discordUser = discordUser
        )
    }
}
