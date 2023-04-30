package ru.astrainteractive.aspekt.commands

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.commands.di.CommandsModule
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload(
    plugin: AspeKt,
    module: CommandsModule
) = plugin.registerCommand("aesreload") {
    val translation by module.translation

    if (!PluginPermission.Reload.hasPermission(sender)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    plugin.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}
