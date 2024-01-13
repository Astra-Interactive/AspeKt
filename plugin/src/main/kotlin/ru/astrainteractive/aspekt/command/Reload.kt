package ru.astrainteractive.aspekt.command

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = plugin.getCommand("aesreload")?.setExecutor { sender, command, label, args ->
    if (!sender.toPermissible().hasPermission(PluginPermission.Reload)) {
        translation.general.noPermission
            .let(kyoriComponentSerializer::toComponent)
            .run(sender::sendMessage)
        return@setExecutor true
    }
    translation.general.reload
        .let(kyoriComponentSerializer::toComponent)
        .run(sender::sendMessage)
    (plugin as AspeKt).reloadPlugin()
    translation.general.reloadComplete
        .let(kyoriComponentSerializer::toComponent)
        .run(sender::sendMessage)
    true
}
