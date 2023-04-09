package ru.astrainteractive.aspekt.commands

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue

fun CommandManager.sit(sitControllerModule: Dependency<SitController>) = AspeKt.instance.registerCommand("sit") {
    val sitController by sitControllerModule
    (sender as? Player)?.let(sitController::toggleSitPlayer)
}