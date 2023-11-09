package ru.astrainteractive.aspekt.command

import ru.astrainteractive.aspekt.command.di.CommandsModule
import ru.astrainteractive.astralibs.string.BukkitTranslationContext

class CommandManager(
    module: CommandsModule,
    translationContext: BukkitTranslationContext
) : CommandsModule by module,
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
        adminPrivate()
        adminPrivateCompleter()
        menuCompleter()
        menu()
    }
}
