package ru.astrainteractive.aspekt.command

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.gui.menu.MenuGui
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.command.registerTabCompleter
import ru.astrainteractive.astralibs.command.types.PrimitiveArgumentType
import ru.astrainteractive.astralibs.util.withEntry

fun CommandManager.menuCompleter() = plugin.registerTabCompleter("menu") {
    when {
        args.size <= 1 -> menuModels.map { it.command }.withEntry(args.getOrNull(0))
        else -> emptyList()
    }
}

fun CommandManager.menu() = plugin.registerCommand("menu") {
    val command = argument(0, PrimitiveArgumentType.String).resultOrNull() ?: return@registerCommand
    val menuModel = menuModels.firstOrNull { it.command == command }
    if (menuModel == null) {
        sender.sendMessage(translation.menuNotFound)
        return@registerCommand
    }
    pluginScope.launch(dispatchers.BukkitAsync) {
        val gui = MenuGui(
            player = sender as Player,
            economyProvider = economyProvider,
            translation = translation,
            menuModel = menuModel,
            dispatchers = dispatchers
        )
        withContext(dispatchers.BukkitMain) {
            gui.open()
        }
    }
}
