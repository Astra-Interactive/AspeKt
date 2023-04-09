package ru.astrainteractive.aspekt.commands

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.utils.HEX

fun CommandManager.tellChat() = AspeKt.instance.registerCommand("tellchat") {
    if (!PluginPermission.TellChat.hasPermission(sender)) return@registerCommand
    argument(0) {
        it?.let(Bukkit::getPlayer)
    }.onSuccess {
        val message = args.slice(1 until args.size).joinToString(" ")
        it.value.sendMessage(message.HEX())
    }
}