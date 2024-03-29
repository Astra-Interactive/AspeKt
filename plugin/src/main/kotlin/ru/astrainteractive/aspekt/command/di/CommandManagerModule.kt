package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.command.CommandManager
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.gui.di.GuiModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface CommandManagerModule : Lifecycle {
    val commandManager: CommandManager

    class Default(
        coreModule: CoreModule,
        eventsModule: EventsModule,
        guiModule: GuiModule,
    ) : CommandManagerModule {
        override val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(
                coreModule = coreModule,
                eventsModule = eventsModule,
                guiModule = guiModule,
            )
            CommandManager(module = dependencies)
        }

        override fun onEnable() {
            commandManager
        }
    }
}
