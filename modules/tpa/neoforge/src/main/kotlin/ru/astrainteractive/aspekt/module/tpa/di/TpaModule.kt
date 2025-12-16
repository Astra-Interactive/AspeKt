package ru.astrainteractive.aspekt.module.tpa.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.tpa.api.TpaApi
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommandExecutor
import ru.astrainteractive.aspekt.module.tpa.command.di.TpaCommandModule
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class TpaModule(
    coreModule: CoreModule,
    commandRegistrarContext: NeoForgeCommandRegistrarContext,
) {
    val tpaCommandExecutor = TpaCommandExecutor(
        translationKrate = coreModule.translation,
        tpaApi = TpaApi(),
        scope = coreModule.ioScope,
        kyoriKrate = coreModule.kyoriKrate,
        minecraftNativeBridge = coreModule.minecraftNativeBridge
    )

    private val commandModule = TpaCommandModule(
        commandRegistrarContext = commandRegistrarContext,
        executor = tpaCommandExecutor
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandModule.lifecycle.onEnable()
        },
    )
}
