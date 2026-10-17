package ru.astrainteractive.aspekt.module.claims.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.di.ClaimCommandModule
import ru.astrainteractive.aspekt.module.claims.event.ForgeClaimEvent
import ru.astrainteractive.aspekt.module.claims.forge.ForgeChunkProvider
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class ForgeClaimModule(
    commandRegistrarContext: CommandRegistrarContext,
    coreModule: CoreModule,
    private val claimModule: ClaimModule
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
