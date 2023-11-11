package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.command.CommandManager
import ru.astrainteractive.aspekt.di.RootModule

interface CommandManagerModule {
    val commandManager: CommandManager

    class Default(rootModule: RootModule) : CommandManagerModule {
        override val commandManager: CommandManager by lazy {
            val dependencies = CommandsDependencies.Default(rootModule)
            CommandManager(
                module = dependencies,
                translationContext = dependencies.translationContext
            )
        }
    }
}
