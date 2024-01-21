package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.job

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.job.di.DiscordLinkJobDependencies

internal class DiscordLinkJob(
    dependencies: DiscordLinkJobDependencies
) : ScheduledJob("DiscordLinkJob"), DiscordLinkJobDependencies by dependencies {

    private val logger = java.util.logging.Logger.getLogger("DiscordLinkJob")

    override val delayMillis: Long
        get() = configuration.discordSRVLink.delay

    override val initialDelayMillis: Long
        get() = configuration.discordSRVLink.initialDelay

    override val isEnabled: Boolean
        get() = configuration.discordSRVLink.isEnabled

    private val guildOrNull: Guild?
        get() = DiscordSRV.getPlugin().mainGuild ?: run {
            logger.warning("Guild was null")
            null
        }

    private suspend fun processDiscordRoles() {
        val guild = guildOrNull ?: return
        val discordRoleId = configuration.discordSRVLink.discordLinkedRole ?: return
        val verifiedDiscordUsers = DiscordSRV.getPlugin()
            .accountLinkManager
            .linkedAccounts
            .keys
            .toSet()
        if (verifiedDiscordUsers.isEmpty()) return

        discordRoleController.removeRoleFromMembersWithRole(
            whitelistedUserIds = verifiedDiscordUsers,
            roleId = discordRoleId,
            guild = guild
        )

        discordRoleController.addRoleToMembers(
            memberIds = verifiedDiscordUsers,
            roleId = discordRoleId,
            guild = guild
        )
    }

    private suspend fun processLuckPermsRoles() {
        val linkedPlayers = DiscordSRV.getPlugin()
            .accountLinkManager
            .linkedAccounts
            .values
            .map(Bukkit::getOfflinePlayer)
            .toSet()
            .onEach { offlinePlayer -> luckPermsRoleController.onLinked(offlinePlayer) }

        Bukkit.getOfflinePlayers()
            .toSet()
            .minus(linkedPlayers)
            .forEach { offlinePlayer -> luckPermsRoleController.onUnLinked(offlinePlayer) }
    }

    override fun execute() {
        scope.launch(dispatchers.IO) {
            supervisorScope {
                async { processDiscordRoles() }
                async { processLuckPermsRoles() }
            }
        }
    }
}
