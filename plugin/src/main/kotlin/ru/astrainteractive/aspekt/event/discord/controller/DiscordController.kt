package ru.astrainteractive.aspekt.event.discord.controller

import github.scarsz.discordsrv.api.events.AccountLinkedEvent
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent
import github.scarsz.discordsrv.dependencies.jda.api.JDA
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role
import github.scarsz.discordsrv.util.DiscordUtil
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.event.discord.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.util.uuid
import java.util.UUID

@Suppress("DuplicatedCode")
class DiscordController(
    module: RoleControllerDependencies,
) : RoleController,
    RoleControllerDependencies by module,
    BukkitTranslationContext by module.translationContext {

    override val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink

    private suspend fun mapRoles(jda: JDA, list: List<String>): List<Role> =
        list.mapNotNull { jda.getRoleById(it) }

    private fun tryAddMoney(uuid: UUID) {
        val player = Bukkit.getPlayer(uuid) ?: return
        val key = "discord.linked.was_before.${player.uuid}"
        val wasLinkedBefore = tempFileManager.fileConfiguration.getBoolean(key, false)
        if (wasLinkedBefore) {
            logger.info("DiscordEvent", "Игрок ${player.name} уже линковал аккаунт, пропускаем выдачу денег")
            return
        }
        logger.info(
            "DiscordEvent",
            "Игроку ${player.name} выдано ${configuration.moneyForLink} за линковку с дискордом"
        )
        tempFileManager.fileConfiguration.set(key, true)
        tempFileManager.save()
        economyProvider?.addMoney(uuid, configuration.moneyForLink.toDouble())
        player.sendMessage(translation.discordLinkReward(configuration.moneyForLink))
    }

    override suspend fun onLinked(e: AccountLinkedEvent) {
        tryAddMoney(e.player.uniqueId)
        logger.info("DiscordEvent", "Игрок ${e.player.name} линкует аккаунт")
        val member = e.user?.id?.let(DiscordUtil::getMemberById) ?: run {
            logger.info("DiscordEvent", "Игрок ${e.player.name} не на нашем сервере")
            return
        }
        mapRoles(e.user.jda, configuration.discordOnLinked.addRoles.value).forEach {
            DiscordUtil.addRoleToMember(member, it)
            logger.info("DiscordEvent", "Игроку ${e.player.name} выдана роль ${it.id}: ${it.name}")
        }

        mapRoles(e.user.jda, configuration.discordOnLinked.removeRoles.value).forEach {
            DiscordUtil.removeRolesFromMember(member, it)
            logger.info("DiscordEvent", "У игрока ${e.player.name} снята роль ${it.id}: ${it.name}")
        }
    }

    override suspend fun onUnLinked(e: AccountUnlinkedEvent) {
        logger.info("DiscordEvent", "Игрок ${e.player.name} отменил линк аккаунта")
        val member = e.discordUser?.id?.let(DiscordUtil::getMemberById) ?: run {
            logger.info("DiscordEvent", "Игрок ${e.player.name} не на нашем сервере")
            return
        }
        mapRoles(e.discordUser.jda, configuration.discordOnUnLinked.addRoles.value).forEach {
            DiscordUtil.addRoleToMember(member, it)
            logger.info("DiscordEvent", "Игроку ${e.player.name} выдана роль ${it.id}: ${it.name}")
        }

        mapRoles(e.discordUser.jda, configuration.discordOnUnLinked.removeRoles.value).forEach {
            DiscordUtil.removeRolesFromMember(member, it)
            logger.info("DiscordEvent", "У игрока ${e.player.name} снята роль ${it.id}: ${it.name}")
        }
    }
}
