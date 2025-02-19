package ru.astrainteractive.aspekt.command

import ru.astrainteractive.aspekt.command.di.CommandsDependencies

class CommandManager(
    module: CommandsDependencies,
) : CommandsDependencies by module {
    init {
        reload()
        atemFrameTabCompleter()
        atemFrame()
        maxOnline()
        tellChat()
        rtp()
        rtpBypassed()
    }
}
