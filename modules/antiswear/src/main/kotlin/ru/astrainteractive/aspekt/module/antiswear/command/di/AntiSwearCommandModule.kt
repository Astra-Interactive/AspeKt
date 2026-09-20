package ru.astrainteractive.aspekt.module.antiswear.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.command.swearfilter.SwearFilterLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for AntiSwear module.
 */
internal class AntiSwearCommandModule(
    swearRepository: SwearRepository,
    coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule,
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = listOf(
        SwearFilterLiteralArgumentBuilder(
            translationKrate = coreModule.translationKrate,
            kyoriKrate = coreModule.kyoriKrate,
            ioScope = coreModule.ioScope,
            swearRepository = swearRepository,
            multiplatformCommand = coreModule.multiplatformCommand,
            platformServer = coreModule.platformServer
        ).create()
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
