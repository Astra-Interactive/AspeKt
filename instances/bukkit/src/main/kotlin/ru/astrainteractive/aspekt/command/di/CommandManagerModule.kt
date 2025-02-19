package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.command.CommandManager
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.sit.di.SitModule
import ru.astrainteractive.aspekt.module.entities.gui.di.GuiModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface CommandManagerModule : Lifecycle {
    val commandManager: CommandManager

    class Default(
        coreModule: CoreModule,
        guiModule: GuiModule,
        sitModule: SitModule,
    ) : CommandManagerModule {
        override val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(
                coreModule = coreModule,
                guiModule = guiModule,
                sitModule = sitModule
            )
            CommandManager(module = dependencies)
        }

        override fun onEnable() {
            commandManager
        }
    }
}
