@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.gui.EntitiesGui
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand

fun CommandManager.entities() = plugin.registerCommand("entities") {
    if (!PluginPermission.Entities.hasPermission(sender)) return@registerCommand
    PluginScope.launch(Dispatchers.IO) {
        val player = sender as? Player ?: return@launch
        EntitiesGui(player, dispatchers).open()
    }
}
