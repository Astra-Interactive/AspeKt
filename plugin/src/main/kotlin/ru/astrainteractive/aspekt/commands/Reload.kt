package ru.astrainteractive.aspekt.commands

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload(
    translationModule: Dependency<PluginTranslation>
) = AspeKt.instance.registerCommand("aesreload") {
    val translation by translationModule
    if (!PluginPermission.Reload.hasPermission(sender)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    AspeKt.instance.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}






