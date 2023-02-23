package ru.astrainteractive.astraessentials.commands

import org.bukkit.Bukkit
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astraessentials.events.sit.SitController
import ru.astrainteractive.astraessentials.plugin.Permission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.commands.registerTabCompleter
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

        // atemframe isVisible isFixed radius
        AstraEssentials.instance.registerTabCompleter("atemframe") {
            when (this.args.size) {
                1 -> listOf("isVisible")
                2 -> listOf("isFixed")
                3 -> listOf("radius")
                else -> emptyList()

            }

        }
        AstraEssentials.instance.registerCommand("atemframe") {
            if (!Permission.AtemFrame.hasPermission(sender)) return@registerCommand
            val player = sender as? Player ?: return@registerCommand
            val isVisible = argument(0) { it == "true" }.successOrNull()?.value ?: true
            val isFixed = argument(1) { it == "true" }.successOrNull()?.value ?: true
            val radius = argument(2) { it?.toDoubleOrNull() }.successOrNull()?.value ?: 20.0
            val itemFrames = player.location.getNearbyEntitiesByType(ItemFrame::class.java, radius)
            player.sendMessage("Params: isVisible: $isVisible; isFixed: $isFixed; radius: $radius; changed ${itemFrames.size} frames")
            itemFrames.forEach {
                it.isFixed = isFixed
                it.isVisible = isVisible
            }
        }
    }
}