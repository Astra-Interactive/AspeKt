package ru.astrainteractive.aspekt.module.claims.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandRegistrar
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.server.location.ChunkProvider
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class ClaimCommandModule(
    executor: ClaimCommandExecutor,
    claimsRepository: ClaimsRepository,
    private val commandRegistrarContext: CommandRegistrarContext,
    coreModule: CoreModule,
    chunkProvider: ChunkProvider
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = ClaimCommandRegistrar(
        claimCommandExecutor = executor,
        claimsRepository = claimsRepository,
        platformServer = coreModule.platformServer,
        multiplatformCommand = coreModule.multiplatformCommand,
        chunkProvider = chunkProvider
    ).createNodes()

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
