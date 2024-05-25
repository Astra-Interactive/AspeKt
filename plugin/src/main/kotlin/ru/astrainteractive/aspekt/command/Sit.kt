package ru.astrainteractive.aspekt.command

import org.bukkit.entity.Player

fun CommandManager.sit() = plugin.getCommand("sit")?.setExecutor { sender, command, label, args ->
    val player = (sender as? Player) ?: return@setExecutor true
    sitController.toggleSitPlayer(
        player = player,
        offset = player.location.add(0.0, -2.0, 0.0)
    )
    true
}
