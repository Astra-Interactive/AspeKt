package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller

import github.scarsz.discordsrv.dependencies.jda.api.entities.User
import net.luckperms.api.LuckPerms
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration

internal class LuckPermsController(
    module: RoleControllerDependencies
) : RoleController, RoleController.Minecraft, RoleControllerDependencies by module {
    private val logger = java.util.logging.Logger.getLogger("LuckPermsController")

    private val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink

    private val api: LuckPerms
        get() = Bukkit.getServicesManager()
            .getRegistration(LuckPerms::class.java)
            ?.provider
            ?: error("LuckPerms not found!")

    private fun addRole(offlinePlayer: OfflinePlayer, role: String?, isSilent: Boolean) {
        role ?: run {
            if (!isSilent) logger.info("minecraftLinkedRole not specified")
            return
        }
        api.userManager.modifyUser(offlinePlayer.uniqueId) {
            val groupNode = InheritanceNode.builder(role).build()
            if (it.nodes.contains(groupNode)) {
                if (!isSilent) {
                    logger.info("Player ${offlinePlayer.name ?: offlinePlayer.uniqueId} already have role $role")
                }
                return@modifyUser
            }
            val result = it.data().add(groupNode)
            if (!isSilent) {
                logger.info("Игроку ${offlinePlayer.name ?: offlinePlayer.uniqueId} выдана роль $role: $result")
            }
        }
    }

    private fun removeRole(offlinePlayer: OfflinePlayer, role: String?, isSilent: Boolean) {
        role ?: run {
            if (!isSilent) logger.info("minecraftLinkedRole not specified")
            return
        }
        api.userManager.modifyUser(offlinePlayer.uniqueId) {
            val groupNode = InheritanceNode.builder(role).build()
            if (!it.nodes.contains(groupNode)) {
                if (!isSilent) {
                    logger.info(
                        "Player ${offlinePlayer.name ?: offlinePlayer.uniqueId} already doesn't have role $role"
                    )
                }
                return@modifyUser
            }
            val result = it.data().remove(groupNode)
            if (!isSilent) {
                logger.info("У игрока ${offlinePlayer.name ?: offlinePlayer.uniqueId} убрана роль $role: $result")
            }
        }
    }

    override suspend fun onLinked(player: OfflinePlayer) {
        addRole(player, configuration.minecraftLinkedRole, true)
    }

    override suspend fun onUnLinked(player: OfflinePlayer) {
        removeRole(player, configuration.minecraftLinkedRole, true)
    }

    override suspend fun onLinked(player: OfflinePlayer, discordUser: User) {
        addRole(player, configuration.minecraftLinkedRole, false)
    }

    override suspend fun onUnLinked(player: OfflinePlayer, discordUser: User) {
        removeRole(player, configuration.minecraftLinkedRole, false)
    }
}
