package ru.astrainteractive.aspekt.command

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.gui.Router
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
        sender.sendMessage(translation.general.menuNotFound)
        return@registerCommand
    }
    val route = Router.Route.Menu(
        player = sender as Player,
        menuModel = menuModel
    )
    router.open(route)
}
