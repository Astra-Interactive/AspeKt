package ru.astrainteractive.aspekt.module.antiswear.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.command.swearfilter.SwearFilterCommandRegistrar
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for AntiSwear module.
 */
internal class AntiSwearCommandModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val swearRepository: SwearRepository
) {
    private val nodes = buildList {
        SwearFilterCommandRegistrar(
            translationKrate = coreModule.translation,
            kyoriKrate = coreModule.kyoriKrate,
            ioScope = coreModule.ioScope,
            swearRepository = swearRepository
        ).createNode().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
