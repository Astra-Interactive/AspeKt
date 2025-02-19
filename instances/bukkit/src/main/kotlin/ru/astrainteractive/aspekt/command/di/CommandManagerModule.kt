package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.command.CommandManager
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface CommandManagerModule : Lifecycle {
    val commandManager: CommandManager

    class Default(coreModule: CoreModule) : CommandManagerModule {
        override val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(
                coreModule = coreModule,
            )
            CommandManager(module = dependencies)
        }

        override fun onEnable() {
            commandManager
        }
    }
}
