package ru.astrainteractive.aspekt.module.claims.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.di.ClaimCommandModule
import ru.astrainteractive.aspekt.module.claims.event.BukkitClaimEvent
import ru.astrainteractive.aspekt.module.claims.server.location.BukkitChunkProvider
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class BukkitClaimModule(
    bukkitCoreModule: BukkitCoreModule,
    claimModule: ClaimModule,
    private val coreModule: CoreModule
) {
    private val claimCommandExecutor = ClaimCommandExecutor(
        scope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translationKrate,
        kyoriKrate = coreModule.kyoriKrate,
        claimsRepository = claimModule.claimsRepository,
        claimErrorMapper = claimModule.claimErrorMapper,
        platformServer = coreModule.platformServer
    )

    private val claimCommandModule = ClaimCommandModule(
        executor = claimCommandExecutor,
        claimsRepository = claimModule.claimsRepository,
        commandRegistrarContext = bukkitCoreModule.commandRegistrarContext,
        coreModule = coreModule,
        chunkProvider = BukkitChunkProvider()
    )

    private val bukkitClaimEvent = BukkitClaimEvent(
        claimsRepository = claimModule.claimsRepository,
        kyoriKrate = coreModule.kyoriKrate,
        translationKrate = coreModule.translationKrate
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            claimCommandModule.lifecycle.onEnable()
            bukkitClaimEvent.onEnable(bukkitCoreModule.plugin)
        },
        onReload = {},
        onDisable = {
            bukkitClaimEvent.onDisable()
        }
    )
}
