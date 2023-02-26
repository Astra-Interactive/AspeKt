package ru.astrainteractive.astraessentials.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astraessentials.gui.EntitiesGui
import ru.astrainteractive.astraessentials.plugin.EPermission
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand

fun CommandManager.entities() = AstraEssentials.instance.registerCommand("entities") {
    if (!EPermission.Entities.hasPermission(sender)) return@registerCommand
    PluginScope.launch(Dispatchers.IO) {
        val player = sender as? Player ?: return@launch
        EntitiesGui(player).open()
    }
}