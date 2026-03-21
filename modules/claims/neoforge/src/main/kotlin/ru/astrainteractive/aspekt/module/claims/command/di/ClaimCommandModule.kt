package ru.astrainteractive.aspekt.module.claims.command.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandRegistrar
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.server.location.ChunkProvider
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for Claims (Forge) module.
 */
class ClaimCommandModule(
    private val executor: ClaimCommandExecutor,
    private val claimsRepository: ClaimsRepository,
    private val commandRegistrarContext: NeoForgeCommandRegistrarContext,
    private val coreModule: CoreModule,
    private val chunkProvider: ChunkProvider
) {
    private val nodes = buildList {
        ClaimCommandRegistrar(
            claimCommandExecutor = executor,
            claimsRepository = claimsRepository,
            multiplatformCommand = coreModule.multiplatformCommand,
            chunkProvider = chunkProvider
        ).createNode().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(commandRegistrarContext::registerWhenReady)
        }
    )
}
