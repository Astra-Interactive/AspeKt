package ru.astrainteractive.aspekt.module.claims.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.di.ClaimCommandModule
import ru.astrainteractive.aspekt.module.claims.event.ForgeClaimEvent
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class ForgeClaimModule(
    commandRegistrarContext: NeoForgeCommandRegistrarContext,
    coreModule: CoreModule,
    claimModule: ClaimModule
) {

    private val claimCommandExecutor = ClaimCommandExecutor(
        scope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translation,
        claimsRepository = claimModule.claimsRepository,
        claimErrorMapper = claimModule.claimErrorMapper,
        kyoriKrate = coreModule.kyoriKrate,
        platformServer = coreModule.platformServer
    )

    @Suppress("UnusedPrivateProperty")
    private val forgeClaimEvent = ForgeClaimEvent(
        translationKrate = coreModule.translation,
        kyoriKrate = coreModule.kyoriKrate,
        claimsRepository = claimModule.claimsRepository
    )

    private val claimCommandModule = ClaimCommandModule(
        executor = claimCommandExecutor,
        claimsRepository = claimModule.claimsRepository,
        commandRegistrarContext = commandRegistrarContext
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = { claimCommandModule.lifecycle.onEnable() },
        onDisable = { claimCommandModule.lifecycle.onDisable() }
    )
}
