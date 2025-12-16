package ru.astrainteractive.aspekt.module.tpa.command.di

import kotlinx.coroutines.flow.onEach
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommandExecutor
import ru.astrainteractive.aspekt.module.tpa.command.tpa.TpaCommandRegistrar
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for TPA (Forge) module.
 */
class TpaCommandModule(
    private val executor: TpaCommandExecutor,
    private val commandRegistrarContext: NeoForgeCommandRegistrarContext
) {
    private val nodes = buildList {
        TpaCommandRegistrar(executor)
            .createNodes()
            .run(::addAll)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(commandRegistrarContext::registerWhenReady)
        }
    )
}
