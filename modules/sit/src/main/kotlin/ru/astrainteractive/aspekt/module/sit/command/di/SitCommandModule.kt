package ru.astrainteractive.aspekt.module.sit.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.sit.command.sit.SitLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for Sit module.
 */
internal class SitCommandModule(
    private val bukkitCoreModule: BukkitCoreModule,
    private val coreModule: CoreModule,
    private val sitController: SitController,
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = buildList {
        SitLiteralArgumentBuilder(
            sitController = sitController,
            multiplatformCommand = coreModule.multiplatformCommand,
        ).create().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
