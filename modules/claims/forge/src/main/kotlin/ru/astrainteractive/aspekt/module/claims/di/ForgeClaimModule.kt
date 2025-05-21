package ru.astrainteractive.aspekt.module.claims.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.event.ForgeClaimEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class ForgeClaimModule(
    registerCommandsEventFlow: Flow<RegisterCommandsEvent>,
    coreModule: CoreModule,
    claimModule: ClaimModule
) {

    private val claimCommandExecutor = ClaimCommandExecutor(
        scope = coreModule.scope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translation,
        claimsRepository = claimModule.claimsRepository,
        claimErrorMapper = claimModule.claimErrorMapper,
        kyoriKrate = coreModule.kyoriComponentSerializer,
        minecraftNativeBridge = coreModule.minecraftNativeBridge
    )

    @Suppress("UnusedPrivateProperty")
    private val forgeClaimEvent = ForgeClaimEvent(
        translationKrate = coreModule.translation,
        kyoriKrate = coreModule.kyoriComponentSerializer,
        claimsRepository = claimModule.claimsRepository
    )
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            coreModule.scope.launch(Dispatchers.IO) {
                registerCommandsEventFlow
                    .first()
                    .claim(
                        claimCommandExecutor = claimCommandExecutor,
                        claimsRepository = claimModule.claimsRepository
                    )
            }
        }
    )
}
