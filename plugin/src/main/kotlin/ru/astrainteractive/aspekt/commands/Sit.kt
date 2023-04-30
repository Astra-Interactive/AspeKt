package ru.astrainteractive.aspekt.commands

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.commands.di.CommandsModule
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue

fun CommandManager.sit(
    plugin: JavaPlugin,
    module: CommandsModule
) = plugin.registerCommand("sit") {
    val sitController by module.sitController

    (sender as? Player)?.let(sitController::toggleSitPlayer)
}
