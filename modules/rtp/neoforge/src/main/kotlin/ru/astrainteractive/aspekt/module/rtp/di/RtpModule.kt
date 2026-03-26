package ru.astrainteractive.aspekt.module.rtp.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.rtp.command.ForgeSafeLocationProvider
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandExecutor
import ru.astrainteractive.aspekt.module.rtp.command.di.RtpCommandModule
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class RtpModule(
    coreModule: CoreModule,
    commandRegistrarContext: NeoForgeCommandRegistrarContext,
) {
    private val executor = RtpCommandExecutor(
        scope = coreModule.ioScope,
        safeLocationProvider = ForgeSafeLocationProvider(),
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translationKrate,
        kyoriKrate = coreModule.kyoriKrate,
    )

    private val commandModule = RtpCommandModule(
        commandRegistrarContext = commandRegistrarContext,
        executor = executor,
        multiplatformCommand = coreModule.multiplatformCommand
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandModule.lifecycle.onEnable()
        }
    )
}
