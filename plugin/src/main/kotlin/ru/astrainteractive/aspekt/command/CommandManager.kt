package ru.astrainteractive.aspekt.command

import ru.astrainteractive.aspekt.command.di.CommandsModule

class CommandManager(module: CommandsModule) : CommandsModule by module {
    init {
        reload()
        sit()
        entities()
        atemFrameTabCompleter()
        atemFrame()
        maxOnline()
        tellChat()
        rtp()
        adminPrivate()
        adminPrivateCompleter()
    }
}
