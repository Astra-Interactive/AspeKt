package ru.astrainteractive.aspekt.module.towny.discord.job

import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.`object`.Resident
import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.util.RestActionExt.asyncResult
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class TownyDiscordRoleJob(
    private val scope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    configurationDependency: Dependency<PluginConfiguration>
) : ScheduledJob("townyDiscordJob") {
    private val logger = java.util.logging.Logger.getLogger("TownyDiscordRoleJob")
    private val configuration by Provider {
        configurationDependency.value.towny.leaderRoleConfiguration
    }
    override val delayMillis: Long
        get() = configuration.delay

    override val initialDelayMillis: Long
        get() = configuration.initialDelay

    override val isEnabled: Boolean
        get() = configuration.isEnabled

    private val guildOrNull: Guild?
        get() = DiscordSRV.getPlugin().mainGuild ?: run {
            logger.warning("Guild was null")
            null
        }

    private val mayorRoleOrNull: Role?
        get() = guildOrNull?.getRoleById(configuration.roleId) ?: run {
            logger.warning("Mayor role with id ${configuration.roleId} not found!")
            null
        }

    private suspend fun giveMayorDiscordRole(mayorsDiscordIds: Set<String>) {
        val mayorRole = mayorRoleOrNull ?: return
        val memberIdsWithNoMayorRole = guildOrNull?.retrieveMembersByIds(*mayorsDiscordIds.toTypedArray())
            ?.get()
            .orEmpty()
            .filterNot { it.roles.contains(mayorRole) }
            .map(Member::getId)

        memberIdsWithNoMayorRole
            .mapNotNull { guildOrNull?.addRoleToMember(it, mayorRole)?.asyncResult() }
            .awaitAll()
    }

    private suspend fun removeMayorRoles(mayorsDiscordIds: Set<String>) {
        val mayorRole = mayorRoleOrNull ?: return
        val memberIdsWithRole = guildOrNull?.getMembersWithRoles(mayorRole)
            .orEmpty()
            .map(Member::getId)
            .filterNot { mayorsDiscordIds.contains(it) }
        val nonMayorDiscordIds = memberIdsWithRole - mayorsDiscordIds

        nonMayorDiscordIds
            .mapNotNull { guildOrNull?.removeRoleFromMember(it, mayorRole)?.asyncResult() }
            .awaitAll()
    }

    override fun execute() {
        scope.launch(dispatchers.IO) {
            supervisorScope {
                val mayorsDiscordIds = TownyAPI.getInstance().residents
                    .filter(Resident::isMayor)
                    .mapNotNull(Resident::getUUID)
                    .toSet()
                    .let(DiscordSRV.getPlugin().accountLinkManager::getManyDiscordIds)
                    .map { entry -> entry.value }
                    .toSet()

                removeMayorRoles(mayorsDiscordIds)
                giveMayorDiscordRole(mayorsDiscordIds)
            }
        }
    }
}
