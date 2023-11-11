@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.command

import org.bukkit.entity.Player
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

fun CommandManager.entities() = plugin.registerCommand("entities") {
    if (!sender.toPermissible().hasPermission(PluginPermission.Entities)) return@registerCommand
    val player = sender as? Player ?: return@registerCommand
    router.open(Router.Route.Entities(player))
}
