package ru.astrainteractive.aspekt.commands

import ru.astrainteractive.aspekt.modules.ServiceLocator


class CommandManager(
    serviceLocator: ServiceLocator,
    controllers: ServiceLocator.Controllers
) {
    init {
        reload(
            translationModule = serviceLocator.TranslationModule
        )
        sit(
            sitControllerModule = controllers.sitControllerModule
        )
        rtp()
        entities()
        maxOnline()
        tellChat()
        atemFrameTabCompleter()
        atemFrame()

    }
}