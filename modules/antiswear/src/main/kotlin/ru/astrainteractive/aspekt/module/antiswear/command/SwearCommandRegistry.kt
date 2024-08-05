package ru.astrainteractive.aspekt.module.antiswear.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.antiswear.command.di.SwearCommandDependencies
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

internal class SwearCommandRegistry(
    dependencies: SwearCommandDependencies
) : SwearCommandDependencies by dependencies {
    private fun createSwearTabCompleter() = plugin.getCommand("swearfilter")?.setTabCompleter { _, _, _, args ->
        when (args.size) {
            1 -> listOf("on", "off")
            2 -> Bukkit.getOnlinePlayers().map(Player::getName)
            else -> emptyList()
        }
    }

    // /swear <off|on> [PLAYER]
    private fun createSwearCommand() = plugin.getCommand("swearfilter")?.setExecutor { sender, command, label, args ->
        val isSwearFilterEnabled = args.getOrNull(0) == "on"
        val playerArgument = args.getOrNull(1)?.let(Bukkit::getPlayer)

        val playerSender = sender as? Player

        val canForceSetSwear = playerSender
            ?.toPermissible()
            ?.hasPermission(PluginPermission.ForcePlayerSwear) == true

        if (playerArgument != null && !canForceSetSwear) {
            sender.sendMessage(kyoriComponentSerializer.toComponent(translation.general.noPermission))
            return@setExecutor true
        }

        if (playerArgument != null) {
            if (isSwearFilterEnabled) {
                translation.swear.swearFilterEnabledFor(playerArgument.name)
            } else {
                translation.swear.swearFilterDisabledFor(playerArgument.name)
            }
        } else {
            if (isSwearFilterEnabled) {
                translation.swear.swearFilterEnabled
            } else {
                translation.swear.swearFilterDisabled
            }
        }.let(kyoriComponentSerializer::toComponent).run(sender::sendMessage)

        val player = playerSender ?: playerArgument ?: run {
            sender.sendMessage(kyoriComponentSerializer.toComponent(translation.general.wrongUsage))
            return@setExecutor true
        }

        scope.launch { swearRepository.setSwearFilterEnabled(player, isSwearFilterEnabled) }
        true
    }

    fun register() {
        createSwearCommand()
        createSwearTabCompleter()
    }
}
