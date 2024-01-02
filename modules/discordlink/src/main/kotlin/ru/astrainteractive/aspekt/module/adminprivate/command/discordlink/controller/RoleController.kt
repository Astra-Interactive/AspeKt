package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller

import github.scarsz.discordsrv.dependencies.jda.api.entities.User
import org.bukkit.OfflinePlayer

internal interface RoleController {
    interface Discord

    interface Minecraft {
        suspend fun onLinked(player: OfflinePlayer)
        suspend fun onUnLinked(player: OfflinePlayer)
    }

    suspend fun onLinked(player: OfflinePlayer, discordUser: User)
    suspend fun onUnLinked(player: OfflinePlayer, discordUser: User)
}
