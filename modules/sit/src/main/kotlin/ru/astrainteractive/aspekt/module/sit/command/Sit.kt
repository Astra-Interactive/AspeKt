package ru.astrainteractive.aspekt.module.sit.command

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController

interface SitCommandDependencies {
    val plugin: JavaPlugin
    val sitController: SitController
}

fun SitCommandDependencies.sit() = plugin.getCommand("sit")?.setExecutor { sender, command, label, args ->
    val player = (sender as? Player) ?: return@setExecutor true
    sitController.toggleSitPlayer(
        player = player,
        locationWithOffset = player.location.add(0.0, -2.0, 0.0)
    )
    true
}
