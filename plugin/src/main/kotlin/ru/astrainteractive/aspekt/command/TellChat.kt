package ru.astrainteractive.aspekt.command

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer

fun CommandManager.tellChat() = plugin.getCommand("tellchat")?.setExecutor { sender, command, label, args ->
    if (!sender.toPermissible().hasPermission(PluginPermission.TellChat)) return@setExecutor true
    val argument = args.getOrNull(0) ?: error("Wrong usage")
    val message = args.slice(1 until args.size).joinToString(" ")

    when (argument) {
        "*" -> Bukkit.getOnlinePlayers().forEach { player ->
            message
                .let(KyoriComponentSerializer.Legacy::toComponent)
                .run(player::sendMessage)
        }

        else -> argument.let(Bukkit::getPlayer)?.sendMessage(KyoriComponentSerializer.Legacy.toComponent(message))
    }
    true
}
