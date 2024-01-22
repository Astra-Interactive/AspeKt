package ru.astrainteractive.aspekt.module.towny.discord.job

import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.`object`.Resident
import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.towny.discord.job.di.TownyDiscordDependencies

internal class TownyDiscordRoleJob(
    dependencies: TownyDiscordDependencies
) : ScheduledJob("townyDiscordJob"), TownyDiscordDependencies by dependencies {
    private val logger = java.util.logging.Logger.getLogger("TownyDiscordRoleJob")
    override val delayMillis: Long
        get() = configuration.towny.leaderRoleConfiguration.delay

    override val initialDelayMillis: Long
        get() = configuration.towny.leaderRoleConfiguration.initialDelay

    override val isEnabled: Boolean
        get() = configuration.towny.leaderRoleConfiguration.isEnabled

    private val guildOrNull: Guild?
        get() = DiscordSRV.getPlugin().mainGuild ?: run {
            logger.warning("Guild was null")
            null
        }

    override fun execute() {
        scope.launch(dispatchers.IO) {
            supervisorScope {
                val guild = guildOrNull ?: return@supervisorScope

                val mayorsDiscordIds = TownyAPI.getInstance().residents
                    .filter(Resident::isMayor)
                    .mapNotNull(Resident::getUUID)
                    .toSet()
                    .let(DiscordSRV.getPlugin().accountLinkManager::getManyDiscordIds)
                    .map { entry -> entry.value }
                    .toSet()

                discordRoleController.removeRoleFromMembersWithRole(
                    whitelistedUserIds = mayorsDiscordIds,
                    roleId = configuration.towny.leaderRoleConfiguration.roleId,
                    guild = guild
                )
                discordRoleController.addRoleToMembers(
                    memberIds = mayorsDiscordIds,
                    roleId = configuration.towny.leaderRoleConfiguration.roleId,
                    guild = guild
                )
            }
        }
    }
}
