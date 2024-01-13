package ru.astrainteractive.aspekt.command

import org.bukkit.entity.Player

fun CommandManager.sit() = plugin.getCommand("sit")?.setExecutor { sender, command, label, args ->
    (sender as? Player)?.let(sitController::toggleSitPlayer)
    true
}
