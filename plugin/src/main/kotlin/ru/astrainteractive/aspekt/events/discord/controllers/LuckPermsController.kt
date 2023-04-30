package ru.astrainteractive.aspekt.events.discord.controllers

import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import net.luckperms.api.LuckPerms
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.logging.Logger

class LuckPermsController(
    pluginConfiguration: Dependency<PluginConfiguration>,
    logger: Dependency<Logger>
) : RoleController {
    private val logger by logger
    private val pluginConfiguration by pluginConfiguration

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
        configuration.luckPermsOnLinked.addRoles.value.forEach { group ->
            e.player.addGroup(group)
        }
        configuration.luckPermsOnLinked.removeRoles.value.forEach { group ->
            e.player.removeGroup(group)
        }
    }

    override suspend fun onUnLinked(e: AccountUnlinkedEvent) {
        configuration.luckPermsOnUnLinked.addRoles.value.forEach { group ->
            e.player.addGroup(group)
        }
        configuration.luckPermsOnUnLinked.removeRoles.value.forEach { group ->
            e.player.removeGroup(group)
        }
    }
}
