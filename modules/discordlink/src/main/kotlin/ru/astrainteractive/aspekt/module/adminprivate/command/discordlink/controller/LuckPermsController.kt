package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller

import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import net.luckperms.api.LuckPerms
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration

internal class LuckPermsController(
    module: RoleControllerDependencies
) : RoleController, RoleControllerDependencies by module {

    override val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink

    private val luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)!!
    private val api = luckPerms.provider
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

    override suspend fun onLinked(e: AccountLinkedEvent) {
        configuration.onLinked.luckPerms.addRoles.forEach { group ->
            e.player.addGroup(group)
        }
        configuration.onLinked.luckPerms.removeRoles.forEach { group ->
            e.player.removeGroup(group)
        }
    }

    override suspend fun onUnLinked(e: AccountUnlinkedEvent) {
        configuration.onUnlinked.luckPerms.addRoles.forEach { group ->
            e.player.addGroup(group)
        }
        configuration.onUnlinked.luckPerms.removeRoles.forEach { group ->
            e.player.removeGroup(group)
        }
    }
}
