package ru.astrainteractive.aspekt.module.sethome.command.di

import ru.astrainteractive.aspekt.module.sethome.command.HomeCommandExecutor
import ru.astrainteractive.aspekt.module.sethome.command.SetHomeLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for SetHome (Forge) module.
 */
class SetHomeCommandModule(
    private val commandRegistrarContext: NeoForgeCommandRegistrarContext,
    private val homeKrateProvider: HomeKrateProvider,
    private val executor: HomeCommandExecutor,
    private val multiplatformCommand: MultiplatformCommand
) {
    private val nodes = buildList {
        SetHomeLiteralArgumentBuilder(
            homeKrateProvider = homeKrateProvider,
            executor = executor,
            multiplatformCommand = multiplatformCommand
        ).create().run(::addAll)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(commandRegistrarContext::registerWhenReady)
        }
    )
}
