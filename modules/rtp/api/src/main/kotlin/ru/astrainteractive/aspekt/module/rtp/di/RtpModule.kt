package ru.astrainteractive.aspekt.module.rtp.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandExecutor
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandRegistrar
import ru.astrainteractive.aspekt.module.rtp.api.SafeLocationProvider
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class RtpModule(
    coreModule: CoreModule,
    private val commandRegistrarContext: CommandRegistrarContext,
    private val safeLocationProvider: SafeLocationProvider,
    private val multiplatformCommand: MultiplatformCommand,
) {

    private val executor = RtpCommandExecutor(
        ioScope = coreModule.ioScope,
        safeLocationProvider = safeLocationProvider,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translationKrate,
        kyoriKrate = coreModule.kyoriKrate,
    )


    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            RtpCommandRegistrar(
                executor = executor,
                safeLocationProvider = safeLocationProvider,
                multiplatformCommand = multiplatformCommand,
                registrarContext = commandRegistrarContext
            ).register()
        }
    )
}
