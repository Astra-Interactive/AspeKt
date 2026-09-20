package ru.astrainteractive.aspekt.module.tpa.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommandExecutor
import ru.astrainteractive.aspekt.module.tpa.command.tpa.TpaCommandRegistrar
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer

internal class TpaCommandModule(
    executor: TpaCommandExecutor,
    platformServer: PlatformServer,
    multiplatformCommand: MultiplatformCommand,
    unconfinedScope: CoroutineScope,
    private val commandRegistrarContext: CommandRegistrarContext
) {
    private val moduleUnconfinedScope = CoroutineScope(unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = TpaCommandRegistrar(
        executor = executor,
        platformServer = platformServer,
        multiplatformCommand = multiplatformCommand
    ).createNodes()

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
