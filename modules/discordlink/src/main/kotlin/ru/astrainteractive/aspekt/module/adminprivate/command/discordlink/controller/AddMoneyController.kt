package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller

import github.scarsz.discordsrv.dependencies.jda.api.entities.User
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.util.uuid
import java.util.UUID

internal class AddMoneyController(
    module: RoleControllerDependencies,
) : RoleController,
    RoleControllerDependencies by module,
    BukkitTranslationContext by module.translationContext {
    private val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink

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
        player.sendMessage(translation.general.discordLinkReward(configuration.moneyForLink))
    }

    override suspend fun onLinked(player: OfflinePlayer, discordUser: User) {
        tryAddMoney(player.uniqueId)
    }

    override suspend fun onUnLinked(player: OfflinePlayer, discordUser: User) = Unit
}
