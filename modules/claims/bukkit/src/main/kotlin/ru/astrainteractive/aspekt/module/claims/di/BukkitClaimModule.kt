package ru.astrainteractive.aspekt.module.claims.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandRegistry
import ru.astrainteractive.aspekt.module.claims.command.di.ClaimCommandDependencies
import ru.astrainteractive.aspekt.module.claims.event.BukkitClaimEvent
import ru.astrainteractive.aspekt.module.claims.event.di.ClaimDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface BukkitClaimModule {
    val lifecycle: Lifecycle

    class Default(
        private val coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        claimModule: ClaimModule
    ) : BukkitClaimModule {

        private val claimCommandRegistry = ClaimCommandRegistry(
            dependencies = ClaimCommandDependencies.Default(
                coreModule = coreModule,
                bukkitCoreModule = bukkitCoreModule,
                claimsRepository = claimModule.claimsRepository,
                claimErrorMapper = claimModule.claimErrorMapper
            )
        )

        private val bukkitClaimEvent = BukkitClaimEvent(
            dependencies = ClaimDependencies.Default(
                coreModule = coreModule,
                bukkitCoreModule = bukkitCoreModule,
                claimsRepository = claimModule.claimsRepository
            )
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                claimCommandRegistry.register()
                bukkitClaimEvent.onEnable(bukkitCoreModule.plugin)
            },
            onReload = {
            },
            onDisable = {
                bukkitClaimEvent.onDisable()
            }
        )
    }
}
