package ru.astrainteractive.aspekt.module.claims.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.di.ClaimCommandModule
import ru.astrainteractive.aspekt.module.claims.event.BukkitClaimEvent
import ru.astrainteractive.aspekt.module.claims.server.location.BukkitChunkProvider
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class BukkitClaimModule(
    bukkitCoreModule: BukkitCoreModule,
    claimModule: ClaimModule,
    private val coreModule: CoreModule
) {
    private val claimCommandModule = ClaimCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        claimsRepository = claimModule.claimsRepository,
        claimErrorMapper = claimModule.claimErrorMapper,
        chunkProvider = BukkitChunkProvider()
    )

    private val bukkitClaimEvent = BukkitClaimEvent(
        claimsRepository = claimModule.claimsRepository,
        kyoriKrate = coreModule.kyoriKrate,
        translationKrate = coreModule.translation
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            claimCommandModule.lifecycle.onEnable()
            bukkitClaimEvent.onEnable(bukkitCoreModule.plugin)
        },
        onReload = {
        },
        onDisable = {
            bukkitClaimEvent.onDisable()
        }
    )
}
