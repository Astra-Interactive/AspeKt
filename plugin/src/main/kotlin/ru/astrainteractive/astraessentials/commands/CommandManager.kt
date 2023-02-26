package ru.astrainteractive.astraessentials.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astraessentials.events.sit.SitController
import ru.astrainteractive.astraessentials.gui.EntitiesGui
import ru.astrainteractive.astraessentials.modules.ServiceLocator
import ru.astrainteractive.astraessentials.plugin.EPermission
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.commands.registerTabCompleter
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.HEX


class CommandManager(
    serviceLocator: ServiceLocator,
    controllers: ServiceLocator.Controllers
) {
    init {
        reload(
            translationModule = serviceLocator.TranslationModule
        )
        sit(
            sitControllerModule = controllers.sitController
        )
        rtp()
        entities()
        maxOnline()
        tellChat()
        atemFrameTabCompleter()
        atemFrame()

    }
}