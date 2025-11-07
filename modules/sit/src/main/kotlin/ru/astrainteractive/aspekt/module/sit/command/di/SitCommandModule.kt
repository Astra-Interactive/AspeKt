package ru.astrainteractive.aspekt.module.sit.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.module.sit.command.sit.SitCommandRegistrar
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for Sit module.
 */
internal class SitCommandModule(
    private val bukkitCoreModule: BukkitCoreModule,
    private val sitController: SitController,
) {
    private val nodes = buildList {
        SitCommandRegistrar(
            sitController = sitController
        ).createNode().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
