package ru.astrainteractive.aspekt.module.claims.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.minecraft.server.MinecraftServer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.controller.ClaimController
import ru.astrainteractive.aspekt.module.claims.controller.di.ClaimControllerDependencies
import ru.astrainteractive.aspekt.module.claims.messenger.ForgeMessenger
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class ForgeClaimModule(
    registerCommandsEventFlow: Flow<RegisterCommandsEvent>,
    serverFlow: Flow<MinecraftServer>,
    private val coreModule: CoreModule,
) {

    private val claimController = ClaimController(
        dependencies = ClaimControllerDependencies.Default(
            coreModule = coreModule,
            folder = coreModule.dataFolder
                .resolve("claims")
                .also(File::mkdirs)
        )
    )
    private val claimCommandExecutor = ClaimCommandExecutor(
        messenger = ForgeMessenger(
            kyoriKrate = coreModule.kyoriComponentSerializer,
            serverFlow = serverFlow,
        ),
        claimController = claimController,
        scope = coreModule.scope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translation
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            GlobalScope.launch(Dispatchers.IO) {
                registerCommandsEventFlow
                    .first()
                    .claim(claimCommandExecutor)
            }
        },
        onReload = {
        },
        onDisable = {
            claimController.cancel()
        }
    )
}
