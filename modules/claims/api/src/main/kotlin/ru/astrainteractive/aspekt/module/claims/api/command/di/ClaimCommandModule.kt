package ru.astrainteractive.aspekt.module.claims.api.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.api.api.ChunkProvider
import ru.astrainteractive.aspekt.module.claims.api.command.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.api.command.ClaimCommandRegistrar
import ru.astrainteractive.aspekt.module.claims.api.di.ClaimModule
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class ClaimCommandModule(
    claimModule: ClaimModule,
    commandRegistrarContext: CommandRegistrarContext,
    coreModule: CoreModule,
    chunkProvider: ChunkProvider
) {
    private val claimCommandExecutor = ClaimCommandExecutor(
        scope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translationKrate,
        claimsRepository = claimModule.claimsRepository,
        claimErrorMapper = claimModule.claimErrorMapper,
        kyoriKrate = coreModule.kyoriKrate,
        platformServer = coreModule.platformServer
    )
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = ClaimCommandRegistrar(
        claimCommandExecutor = claimCommandExecutor,
        claimsRepository = claimModule.claimsRepository,
        platformServer = coreModule.platformServer,
        multiplatformCommand = coreModule.multiplatformCommand,
        chunkProvider = chunkProvider,
        commandExceptionHandler = coreModule.commandExceptionHandler
    ).createNodes()

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
