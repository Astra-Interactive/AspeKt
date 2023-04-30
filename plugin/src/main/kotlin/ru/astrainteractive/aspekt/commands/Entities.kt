@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.commands.di.CommandsModule
import ru.astrainteractive.aspekt.gui.EntitiesGui
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue

fun CommandManager.entities(
    plugin: JavaPlugin,
    module: CommandsModule
) = plugin.registerCommand("entities") {
    val dispatchers by module.dispatchers

    if (!PluginPermission.Entities.hasPermission(sender)) return@registerCommand
    PluginScope.launch(Dispatchers.IO) {
        val player = sender as? Player ?: return@launch
        EntitiesGui(player, dispatchers).open()
    }
}
