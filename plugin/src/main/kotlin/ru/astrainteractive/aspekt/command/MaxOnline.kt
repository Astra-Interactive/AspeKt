package ru.astrainteractive.aspekt.command

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

fun CommandManager.maxOnline() = plugin.registerCommand("maxonline") {
    if (!sender.toPermissible().hasPermission(PluginPermission.MaxOnline)) return@registerCommand
    argument(0) {
        it.toIntOrNull() ?: Bukkit.getServer().maxPlayers
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
