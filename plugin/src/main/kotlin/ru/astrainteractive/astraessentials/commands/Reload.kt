package ru.astrainteractive.astraessentials.commands

import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astraessentials.modules.TranslationModule
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astraessentials.utils.Permission

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = AstraEssentials.instance.registerCommand("atempreload") {
    val translation = TranslationModule.value
    if (!Permission.Reload.hasPermission(sender)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    AstraEssentials.instance.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}






