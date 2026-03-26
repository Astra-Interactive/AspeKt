package ru.astrainteractive.aspekt.module.antiswear.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.command.swearfilter.SwearFilterLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for AntiSwear module.
 */
internal class AntiSwearCommandModule(
    swearRepository: SwearRepository,
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule,
) {
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
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
