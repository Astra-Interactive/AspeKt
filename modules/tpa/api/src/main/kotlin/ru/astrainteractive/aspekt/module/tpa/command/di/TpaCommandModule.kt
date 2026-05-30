package ru.astrainteractive.aspekt.module.tpa.command.di

import ru.astrainteractive.aspekt.module.tpa.command.TpaCommandExecutor
import ru.astrainteractive.aspekt.module.tpa.command.tpa.TpaCommandRegistrar
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer

internal class TpaCommandModule(
    private val executor: TpaCommandExecutor,
    private val platformServer: PlatformServer,
    private val commandRegistrarContext: CommandRegistrarContext,
    private val multiplatformCommand: MultiplatformCommand
) {
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            TpaCommandRegistrar(
                executor = executor,
                platformServer = platformServer,
                multiplatformCommand = multiplatformCommand,
                registrarContext = commandRegistrarContext
            ).register()
        }
    )
}
