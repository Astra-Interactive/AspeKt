package ru.astrainteractive.astraessentials.commands

import org.bukkit.entity.Player
import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astraessentials.events.sit.SitController
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue

fun CommandManager.sit(sitControllerModule: Dependency<SitController>) = AstraEssentials.instance.registerCommand("sit") {
    val sitController by sitControllerModule
    (sender as? Player)?.let(sitController::toggleSitPlayer)
}