package ru.astrainteractive.astraessentials.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astraessentials.events.sit.SitController
import ru.astrainteractive.astraessentials.plugin.Permission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.utils.HEX


class CommandManager {

    init {
        reload()
        AstraEssentials.instance.registerCommand("rtp") {
            sender.sendMessage("#db2c18Возможно, вы хотели ввести /tpr".HEX())
        }
        AstraEssentials.instance.registerCommand("sit") {
            (sender as? Player)?.let(SitController::toggleSitPlayer)
        }

        AstraEssentials.instance.registerCommand("tellchat") {
            if (!Permission.TellChat.hasPermission(sender)) return@registerCommand
            argument(0) {
                it?.let(Bukkit::getPlayer)
            }.onSuccess {
                val message = args.slice(1 until args.size).joinToString(" ")
                it.value.sendMessage(message.HEX())
            }
        }
    }
}