package ru.astrainteractive.aspekt.commands

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.utils.hex

fun CommandManager.tellChat(plugin: JavaPlugin) = plugin.registerCommand("tellchat") {
    if (!PluginPermission.TellChat.hasPermission(sender)) return@registerCommand
    argument(0) {
        it?.let(Bukkit::getPlayer)
    }.onSuccess {
        val message = args.slice(1 until args.size).joinToString(" ")
        it.value.sendMessage(message.hex())
    }
}
