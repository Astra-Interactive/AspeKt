package ru.astrainteractive.aspekt.module.sit.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.core.di.BukkitCoreModule
import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.module.sit.command.SitLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.sit.event.SitController
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal class SitCommandModule(
    bukkitCoreModule: BukkitCoreModule,
    coreModule: CoreModule,
    sitController: SitController,
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = buildList {
        SitLiteralArgumentBuilder(
            toggleSitPlayer = { player, locationWithOffset ->
                sitController.toggleSitPlayer(player = player, locationWithOffset = locationWithOffset)
            },
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create().run(::add)
    }

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
