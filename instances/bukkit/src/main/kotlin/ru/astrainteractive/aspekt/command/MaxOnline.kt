package ru.astrainteractive.aspekt.command

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

fun CommandManager.maxOnline() = plugin.getCommand("maxonline")?.setExecutor { sender, command, label, args ->
    if (!sender.toPermissible().hasPermission(PluginPermission.MaxOnline)) return@setExecutor true
    val argument = args.getOrNull(0)?.toIntOrNull() ?: Bukkit.getServer().maxPlayers

    if (argument < 30) {
        sender.sendMessage("$argument is less than 30")
    } else {
        sender.sendMessage("Max online now is $argument")
        Bukkit.getServer().maxPlayers = argument
    }
    true
}
