package ru.astrainteractive.aspekt.command

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.util.hex

fun CommandManager.tellChat() = plugin.registerCommand("tellchat") {
    if (!sender.toPermissible().hasPermission(PluginPermission.TellChat)) return@registerCommand
    val argument = args.getOrNull(0) ?: error("Wrong usage")
    val message = args.slice(1 until args.size).joinToString(" ")

    when (argument) {
        "*" -> Bukkit.getOnlinePlayers().forEach { player ->
            player.sendMessage(message.hex())
        }

        else -> argument.let(Bukkit::getPlayer)?.sendMessage(message.hex())
    }
}
