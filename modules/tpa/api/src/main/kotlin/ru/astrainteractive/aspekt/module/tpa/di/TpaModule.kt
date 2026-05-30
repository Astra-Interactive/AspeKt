package ru.astrainteractive.aspekt.module.tpa.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.tpa.api.TpaApi
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommandExecutor
import ru.astrainteractive.aspekt.module.tpa.command.di.TpaCommandModule
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class TpaModule(
    coreModule: CoreModule,
    commandRegistrarContext: CommandRegistrarContext,
) {
    private val tpaCommandExecutor = TpaCommandExecutor(
        translationKrate = coreModule.translationKrate,
        tpaApi = TpaApi(),
        scope = coreModule.ioScope,
        platformServer = coreModule.platformServer,
        kyoriKrate = coreModule.kyoriKrate,
    )

    private val commandModule = TpaCommandModule(
        commandRegistrarContext = commandRegistrarContext,
        executor = tpaCommandExecutor,
        platformServer = coreModule.platformServer,
        multiplatformCommand = coreModule.multiplatformCommand
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandModule.lifecycle.onEnable()
        },
    )
}
