package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.command.CommandManager
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.gui.di.GuiModule
import ru.astrainteractive.aspekt.module.menu.di.MenuModule
import ru.astrainteractive.aspekt.util.Lifecycle

interface CommandManagerModule : Lifecycle {
    val commandManager: CommandManager

    class Default(
        coreModule: CoreModule,
        eventsModule: EventsModule,
        guiModule: GuiModule,
        menuModule: MenuModule
    ) : CommandManagerModule {
        override val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(
                coreModule = coreModule,
                eventsModule = eventsModule,
                guiModule = guiModule,
                menuModule = menuModule
            )
            CommandManager(
                module = dependencies,
                translationContext = dependencies.translationContext
            )
        }

        override fun onEnable() {
            commandManager
        }
    }
}
