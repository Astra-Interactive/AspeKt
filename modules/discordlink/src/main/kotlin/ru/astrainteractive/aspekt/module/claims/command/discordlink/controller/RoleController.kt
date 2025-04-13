package ru.astrainteractive.aspekt.module.claims.command.discordlink.controller

import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild
import github.scarsz.discordsrv.dependencies.jda.api.entities.User
import org.bukkit.OfflinePlayer

interface RoleController {

    interface Minecraft : RoleController {
        suspend fun onLinked(player: OfflinePlayer)
        suspend fun onUnLinked(player: OfflinePlayer)
    }

    interface Discord : RoleController {
        suspend fun removeRoleFromMembersWithRole(
            whitelistedUserIds: Set<String>,
            roleId: String,
            guild: Guild
        )

        suspend fun addRoleToMembers(
            memberIds: Set<String>,
            roleId: String,
            guild: Guild
        )
    }

    suspend fun onLinked(player: OfflinePlayer, discordUser: User)
    suspend fun onUnLinked(player: OfflinePlayer, discordUser: User)
}
