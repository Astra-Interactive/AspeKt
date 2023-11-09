package ru.astrainteractive.aspekt.command

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.util.hex

fun CommandManager.tellChat() = plugin.registerCommand("tellchat") {
    if (!sender.toPermissible().hasPermission(PluginPermission.TellChat)) return@registerCommand
    argument(0) {
        it.let(Bukkit::getPlayer) ?: error("$it not a player")
    }.onSuccess {
        val message = args.slice(1 until args.size).joinToString(" ")
        it.value.sendMessage(message.hex())
    }
}
