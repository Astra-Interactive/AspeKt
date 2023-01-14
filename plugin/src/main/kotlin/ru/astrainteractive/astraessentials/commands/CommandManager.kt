package ru.astrainteractive.astraessentials.commands

import org.bukkit.entity.Player
import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astraessentials.events.sit.SitController
import ru.astrainteractive.astralibs.commands.registerCommand


class CommandManager {

    init {
        reload()
        AstraEssentials.instance.registerCommand("sit") {
            (sender as? Player)?.let(SitController::toggleSitPlayer)
        }
    }
}