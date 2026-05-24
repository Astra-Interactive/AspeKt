package ru.astrainteractive.aspekt.module.rtp.command.di

import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandExecutor
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandRegistrar
import ru.astrainteractive.aspekt.module.rtp.command.SafeLocationProvider
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class RtpCommandModule(
    private val commandRegistrarContext: CommandRegistrarContext,
    private val executor: RtpCommandExecutor,
    private val safeLocationProvider: SafeLocationProvider,
    private val multiplatformCommand: MultiplatformCommand
) {
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
