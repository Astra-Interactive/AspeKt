package ru.astrainteractive.aspekt.command

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.commands.registerCommand

fun CommandManager.sit() = plugin.registerCommand("sit") {
    (sender as? Player)?.let(sitController::toggleSitPlayer)
}