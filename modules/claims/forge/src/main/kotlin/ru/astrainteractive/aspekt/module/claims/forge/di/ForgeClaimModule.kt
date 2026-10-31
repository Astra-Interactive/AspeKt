package ru.astrainteractive.aspekt.module.claims.forge.di

import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.api.command.di.ClaimCommandModule
import ru.astrainteractive.aspekt.module.claims.api.di.ClaimModule
import ru.astrainteractive.aspekt.module.claims.forge.chunk.ForgeChunkProvider
import ru.astrainteractive.aspekt.module.claims.forge.event.ForgeClaimEvent
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class ForgeClaimModule(
    commandRegistrarContext: CommandRegistrarContext,
    coreModule: CoreModule,
    claimModule: ClaimModule
) {

    @Suppress("UnusedPrivateProperty")
    private val forgeClaimEvent = ForgeClaimEvent(
        translationKrate = coreModule.translationKrate,
        claimsRepository = claimModule.claimsRepository
    )

    private val claimCommandModule = ClaimCommandModule(
        claimModule = claimModule,
        commandRegistrarContext = commandRegistrarContext,
        coreModule = coreModule,
        chunkProvider = ForgeChunkProvider()
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            claimModule.lifecycle.onEnable()
            claimCommandModule.lifecycle.onEnable()
        },
        onDisable = {
            claimCommandModule.lifecycle.onDisable()
            claimModule.lifecycle.onDisable()
        }
    )
}
