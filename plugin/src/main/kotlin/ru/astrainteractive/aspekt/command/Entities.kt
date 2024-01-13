@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.command

import org.bukkit.entity.Player
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

fun CommandManager.entities() = plugin.getCommand("entities")?.setExecutor { sender, command, label, args ->
    if (!sender.toPermissible().hasPermission(PluginPermission.Entities)) return@setExecutor true
    val player = sender as? Player ?: return@setExecutor true
    router.open(Router.Route.Entities(player))
    true
}
