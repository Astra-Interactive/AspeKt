package ru.astrainteractive.aspekt.command

import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = plugin.registerCommand("aesreload") {
    if (!sender.toPermissible().hasPermission(PluginPermission.Reload)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    plugin.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}
