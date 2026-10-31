package ru.astrainteractive.aspekt.module.tpa.api.di

import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.module.tpa.api.api.TpaApi
import ru.astrainteractive.aspekt.module.tpa.api.command.TpaCommandExecutor
import ru.astrainteractive.aspekt.module.tpa.api.command.di.TpaCommandModule
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class TpaModule(
    coreModule: CoreModule,
    commandRegistrarContext: CommandRegistrarContext,
) {
    private val tpaCommandExecutor = TpaCommandExecutor(
        translationKrate = coreModule.translationKrate,
        tpaApi = TpaApi(),
        scope = coreModule.ioScope,
        platformServer = coreModule.platformServer,
    )

    private val commandModule = TpaCommandModule(
        commandRegistrarContext = commandRegistrarContext,
        executor = tpaCommandExecutor,
        platformServer = coreModule.platformServer,
        multiplatformCommand = coreModule.multiplatformCommand,
        commandExceptionHandler = coreModule.commandExceptionHandler,
        unconfinedScope = coreModule.unconfinedScope
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandModule.lifecycle.onEnable()
        },
        onDisable = {
            commandModule.lifecycle.onDisable()
        }
    )

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("tpa.yml")
    }
}
