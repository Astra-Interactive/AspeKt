package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.command.CommandManager
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.gui.di.GuiModule
import ru.astrainteractive.aspekt.util.Lifecycle

interface CommandManagerModule : Lifecycle {
    val commandManager: CommandManager

    class Default(
        coreModule: CoreModule,
        eventsModule: EventsModule,
        adminPrivateModule: AdminPrivateModule,
        guiModule: GuiModule
    ) : CommandManagerModule {
        override val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(
                coreModule = coreModule,
                eventsModule = eventsModule,
                adminPrivateModule = adminPrivateModule,
                guiModule = guiModule
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