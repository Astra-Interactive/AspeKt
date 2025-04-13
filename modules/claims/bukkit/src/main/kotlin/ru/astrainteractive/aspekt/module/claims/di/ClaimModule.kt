package ru.astrainteractive.aspekt.module.claims.di

import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandRegistry
import ru.astrainteractive.aspekt.module.claims.command.di.ClaimCommandDependencies
import ru.astrainteractive.aspekt.module.claims.controller.ClaimController
import ru.astrainteractive.aspekt.module.claims.controller.di.ClaimControllerDependencies
import ru.astrainteractive.aspekt.module.claims.event.BukkitClaimEvent
import ru.astrainteractive.aspekt.module.claims.event.di.ClaimDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

interface ClaimModule {
    val lifecycle: Lifecycle

    class Default(
        private val coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : ClaimModule {
        private val claimController = ClaimController(
            dependencies = ClaimControllerDependencies.Default(
                coreModule = coreModule,
                folder = coreModule.dataFolder
                    .resolve("claims")
                    .also(File::mkdirs)
            )
        )

        private val claimCommandRegistry = ClaimCommandRegistry(
            dependencies = ClaimCommandDependencies.Default(
                coreModule = coreModule,
                claimController = claimController,
                bukkitCoreModule = bukkitCoreModule
            )
        )

        private val bukkitClaimEvent = BukkitClaimEvent(
            dependencies = ClaimDependencies.Default(
                coreModule = coreModule,
                claimController = claimController,
                bukkitCoreModule = bukkitCoreModule
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
                claimController.cancel()
            }
        )
    }
}
