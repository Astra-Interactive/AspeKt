package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller

import github.scarsz.discordsrv.dependencies.jda.api.entities.User
import net.luckperms.api.LuckPerms
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal class LuckPermsController(
    module: RoleControllerDependencies
) : RoleController, RoleController.Minecraft, RoleControllerDependencies by module {

    private val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink

    private val api by Provider {
        Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)?.provider ?: error("LuckPerms not found!")
    }

    private fun OfflinePlayer.addGroup(group: String) {
        api.userManager.modifyUser(uniqueId) {
            val groupNode = InheritanceNode.builder(group).build()
            val result = it.data().add(groupNode)
            logger.info("DiscordEvent", "Игроку $name выдана роль $group: $result")
        }
    }

    private fun OfflinePlayer.removeGroup(group: String) {
        api.userManager.modifyUser(uniqueId) {
            val groupNode = InheritanceNode.builder(group).build()
            val result = it.data().remove(groupNode)
            logger.info("DiscordEvent", "У игрока $name убрана роль $group: $result")
        }
    }

    override suspend fun onLinked(player: OfflinePlayer) {
        configuration.onLinked.luckPerms.addRoles.forEach { group ->
            player.addGroup(group)
        }
        configuration.onLinked.luckPerms.removeRoles.forEach { group ->
            player.removeGroup(group)
        }
    }

    override suspend fun onUnLinked(player: OfflinePlayer) {
        configuration.onUnlinked.luckPerms.addRoles.forEach { group ->
            player.addGroup(group)
        }
        configuration.onUnlinked.luckPerms.removeRoles.forEach { group ->
            player.removeGroup(group)
        }
    }

    override suspend fun onLinked(player: OfflinePlayer, discordUser: User) = onLinked(player)

    override suspend fun onUnLinked(player: OfflinePlayer, discordUser: User) = onUnLinked(player)
}
