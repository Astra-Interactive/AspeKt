package ru.astrainteractive.aspekt.module.claims.command.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandRegistrar
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.server.location.ChunkProvider
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class ClaimCommandModule(
    private val executor: ClaimCommandExecutor,
    private val claimsRepository: ClaimsRepository,
    private val commandRegistrarContext: CommandRegistrarContext,
    private val coreModule: CoreModule,
    private val chunkProvider: ChunkProvider
) {
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            ClaimCommandRegistrar(
                claimCommandExecutor = executor,
                claimsRepository = claimsRepository,
                platformServer = coreModule.platformServer,
                multiplatformCommand = coreModule.multiplatformCommand,
                registrarContext = commandRegistrarContext,
                chunkProvider = chunkProvider
            ).register()
        }
    )
}
