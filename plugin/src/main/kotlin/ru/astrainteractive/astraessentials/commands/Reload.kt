package ru.astrainteractive.astraessentials.commands

import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astraessentials.plugin.PluginPermission
import ru.astrainteractive.astraessentials.plugin.PluginTranslation
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
) = AstraEssentials.instance.registerCommand("aesreload") {
    val translation by translationModule
    if (!PluginPermission.Reload.hasPermission(sender)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    AstraEssentials.instance.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}






