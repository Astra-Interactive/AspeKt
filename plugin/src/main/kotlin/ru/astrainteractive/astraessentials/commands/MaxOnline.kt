package ru.astrainteractive.astraessentials.commands

import org.bukkit.Bukkit
import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astraessentials.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand

fun CommandManager.maxOnline() = AstraEssentials.instance.registerCommand("maxonline") {
    if (!PluginPermission.MaxOnline.hasPermission(sender)) return@registerCommand
    argument(0) {
        it?.toIntOrNull()
    }.onFailure {
        sender.sendMessage("/maxonline 10")
    }.onSuccess {
        if (it.value < 30)
            sender.sendMessage("${it.value} is less than 30")
        else {
            sender.sendMessage("Max online now is ${it.value}")
            Bukkit.getServer().maxPlayers = it.value
        }
    }

}