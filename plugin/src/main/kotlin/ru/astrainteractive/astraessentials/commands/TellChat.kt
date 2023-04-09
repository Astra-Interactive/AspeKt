package ru.astrainteractive.astraessentials.commands

import org.bukkit.Bukkit
import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astraessentials.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.utils.HEX

fun CommandManager.tellChat() = AstraEssentials.instance.registerCommand("tellchat") {
    if (!PluginPermission.TellChat.hasPermission(sender)) return@registerCommand
    argument(0) {
        it?.let(Bukkit::getPlayer)
    }.onSuccess {
        val message = args.slice(1 until args.size).joinToString(" ")
        it.value.sendMessage(message.HEX())
    }
}