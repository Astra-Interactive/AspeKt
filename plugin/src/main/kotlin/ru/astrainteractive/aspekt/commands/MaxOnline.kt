package ru.astrainteractive.aspekt.commands

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand

fun CommandManager.maxOnline(plugin: JavaPlugin) = plugin.registerCommand("maxonline") {
    if (!PluginPermission.MaxOnline.hasPermission(sender)) return@registerCommand
    argument(0) {
        it?.toIntOrNull()
    }.onFailure {
        sender.sendMessage("/maxonline 10")
    }.onSuccess {
        if (it.value < 30) {
            sender.sendMessage("${it.value} is less than 30")
        } else {
            sender.sendMessage("Max online now is ${it.value}")
            Bukkit.getServer().maxPlayers = it.value
        }
    }
}
