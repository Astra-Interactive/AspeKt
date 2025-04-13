package ru.astrainteractive.aspekt.module.claims.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.minecraft.server.MinecraftServer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.event.ForgeClaimEvent
import ru.astrainteractive.aspekt.module.claims.messenger.ForgeMessenger
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class ForgeClaimModule(
    registerCommandsEventFlow: Flow<RegisterCommandsEvent>,
    serverFlow: Flow<MinecraftServer>,
    coreModule: CoreModule,
    claimModule: ClaimModule
) {

    private val claimCommandExecutor = ClaimCommandExecutor(
        messenger = ForgeMessenger(
            kyoriKrate = coreModule.kyoriComponentSerializer,
            serverFlow = serverFlow,
            scope = coreModule.scope
        ),
        claimController = claimModule.claimController,
        scope = coreModule.scope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translation,
        claimsRepository = claimModule.claimsRepository
    )

    @Suppress("UnusedPrivateProperty")
    private val forgeClaimEvent = ForgeClaimEvent(
        claimController = claimModule.claimController,
        translationKrate = coreModule.translation,
        kyoriKrate = coreModule.kyoriComponentSerializer
    )
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            coreModule.scope.launch(Dispatchers.IO) {
                registerCommandsEventFlow
                    .first()
                    .claim(
                        claimCommandExecutor = claimCommandExecutor,
                        minecraftServer = serverFlow.first()
                    )
            }
        }
    )
}
