package ru.astrainteractive.aspekt.module.claims.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimErrorMapper
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.server.location.ChunkProvider
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal class ClaimCommandModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val claimsRepository: ClaimsRepository,
    private val claimErrorMapper: ClaimErrorMapper,
    private val chunkProvider: ChunkProvider
) {

    private val executor = ClaimCommandExecutor(
        scope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translation,
        kyoriKrate = coreModule.kyoriKrate,
        claimsRepository = claimsRepository,
        claimErrorMapper = claimErrorMapper,
        platformServer = coreModule.platformServer
    )

    private val nodes = buildList {
        ClaimLiteralArgumentBuilder(
            executor = executor,
            translationKrate = coreModule.translation,
            kyoriKrate = coreModule.kyoriKrate,
            chunkProvider = chunkProvider,
            multiplatformCommand = coreModule.multiplatformCommand
        ).create().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
