package ru.astrainteractive.aspekt.module.claims.bukkit.di

import ru.astrainteractive.aspekt.core.di.BukkitCoreModule
import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.api.command.di.ClaimCommandModule
import ru.astrainteractive.aspekt.module.claims.api.di.ClaimModule
import ru.astrainteractive.aspekt.module.claims.bukkit.chunk.BukkitChunkProvider
import ru.astrainteractive.aspekt.module.claims.bukkit.event.BukkitClaimEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class BukkitClaimModule(
    bukkitCoreModule: BukkitCoreModule,
    claimModule: ClaimModule,
    coreModule: CoreModule
) {
    private val claimCommandModule = ClaimCommandModule(
        claimModule = claimModule,
        commandRegistrarContext = bukkitCoreModule.commandRegistrarContext,
        coreModule = coreModule,
        chunkProvider = BukkitChunkProvider()
    )

    private val bukkitClaimEvent = BukkitClaimEvent(
        claimsRepository = claimModule.claimsRepository,
        translationKrate = coreModule.translationKrate
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            claimModule.lifecycle.onEnable()
            claimCommandModule.lifecycle.onEnable()
            bukkitClaimEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            bukkitClaimEvent.onDisable()
            claimCommandModule.lifecycle.onDisable()
            claimModule.lifecycle.onDisable()
        }
    )
}
