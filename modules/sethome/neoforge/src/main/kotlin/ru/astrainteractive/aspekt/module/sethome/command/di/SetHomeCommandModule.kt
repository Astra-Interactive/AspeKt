package ru.astrainteractive.aspekt.module.sethome.command.di

import kotlinx.coroutines.flow.onEach
import ru.astrainteractive.aspekt.module.sethome.command.HomeCommandExecutor
import ru.astrainteractive.aspekt.module.sethome.command.SetHomeCommandRegistrar
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for SetHome (Forge) module.
 */
class SetHomeCommandModule(
    private val commandRegistrarContext: NeoForgeCommandRegistrarContext,
    private val homeKrateProvider: HomeKrateProvider,
    private val executor: HomeCommandExecutor
) {
    private val nodes = buildList {
        SetHomeCommandRegistrar(
            homeKrateProvider = homeKrateProvider,
            executor = executor
        ).createNodes().run(::addAll)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(commandRegistrarContext::registerWhenReady)
        }
    )
}
