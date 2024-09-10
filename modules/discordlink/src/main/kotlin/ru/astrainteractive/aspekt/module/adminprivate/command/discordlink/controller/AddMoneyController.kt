package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller

import github.scarsz.discordsrv.dependencies.jda.api.entities.User
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.async.AsyncComponent
import java.util.UUID

internal class AddMoneyController(
    module: RoleControllerDependencies,
) : RoleController,
    RoleControllerDependencies by module,
    AsyncComponent() {
    private val logger = java.util.logging.Logger.getLogger("AddMoneyController")

    private val configuration: PluginConfiguration.DiscordSRVLink
        get() = pluginConfiguration.discordSRVLink

    private fun tryAddMoney(uuid: UUID) {
        val player = Bukkit.getPlayer(uuid) ?: return
        val key = "discord.linked.was_before.${player.uniqueId}"
        val wasLinkedBefore = tempFileConfiguration.getBoolean(key, false)
        if (wasLinkedBefore) {
            logger.info("Игрок ${player.name} уже линковал аккаунт, пропускаем выдачу денег")
            return
        }
        logger.info("Игроку ${player.name} выдано ${configuration.moneyForLink} за линковку с дискордом")
        tempFileConfiguration.set(key, true)
        tempFileConfiguration.save(tempFile)
        launch { economyProvider?.addMoney(uuid, configuration.moneyForLink.toDouble()) }
        translation.general.discordLinkReward(configuration.moneyForLink)
            .let(kyoriComponentSerializer::toComponent)
            .run(player::sendMessage)
    }

    override suspend fun onLinked(player: OfflinePlayer, discordUser: User) {
        tryAddMoney(player.uniqueId)
    }

    override suspend fun onUnLinked(player: OfflinePlayer, discordUser: User) = Unit
}
