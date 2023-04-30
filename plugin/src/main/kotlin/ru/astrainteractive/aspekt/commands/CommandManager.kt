package ru.astrainteractive.aspekt.commands

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.commands.di.CommandsModule

class CommandManager(
    module: CommandsModule,
    plugin: AspeKt
) {
    init {
        reload(
            plugin,
            module
        )
        sit(
            plugin,
            module
        )
        entities(
            plugin,
            module
        )
        atemFrameTabCompleter(plugin)
        atemFrame(plugin)
        maxOnline(plugin)
        tellChat(plugin)
        rtp(plugin)
    }
}
