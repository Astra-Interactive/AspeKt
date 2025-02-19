package ru.astrainteractive.aspekt.command

import ru.astrainteractive.aspekt.command.di.CommandsDependencies
import ru.astrainteractive.aspekt.module.entities.command.entities

class CommandManager(
    module: CommandsDependencies,
) : CommandsDependencies by module {
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
    }
}
