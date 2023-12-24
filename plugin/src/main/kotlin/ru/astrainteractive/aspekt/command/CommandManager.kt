package ru.astrainteractive.aspekt.command

import ru.astrainteractive.aspekt.command.di.CommandsDependencies
import ru.astrainteractive.aspekt.module.menu.command.MenuCommandFactory
import ru.astrainteractive.astralibs.string.BukkitTranslationContext

class CommandManager(
    module: CommandsDependencies,
    translationContext: BukkitTranslationContext
) : CommandsDependencies by module,
    BukkitTranslationContext by translationContext {
    init {
        reload()
        sit()
        entities()
        atemFrameTabCompleter()
        atemFrame()
        maxOnline()
        tellChat()
        rtp()
        rtpBypassed()
        MenuCommandFactory(
            plugin = plugin,
            translationContext = translationContext,
            menuModelProvider = { menuModels },
            translationProvider = { translation },
            menuRouter = { menuRouter }
        ).create()
    }
}
