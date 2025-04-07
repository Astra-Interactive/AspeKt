package ru.astrainteractive.aspekt.module.adminprivate.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.minecraft.server.MinecraftServer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.adminPrivate
import ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate.AdminPrivateCommandExecutor
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.messenger.ForgeMessenger
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class ForgeClaimModule(
    registerCommandsEventFlow: Flow<RegisterCommandsEvent>,
    serverFlow: Flow<MinecraftServer>,
    private val coreModule: CoreModule,
) {

    private val adminPrivateController = AdminPrivateController(
        dependencies = AdminPrivateControllerDependencies.Default(
            coreModule = coreModule,
            folder = coreModule.dataFolder
                .resolve("claims")
                .also(File::mkdirs)
        )
    )
    private val adminPrivateCommandExecutor = AdminPrivateCommandExecutor(
        messenger = ForgeMessenger(
            kyoriKrate = coreModule.kyoriComponentSerializer,
            serverFlow = serverFlow,
        ),
        adminPrivateController = adminPrivateController,
        scope = coreModule.scope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translation
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            GlobalScope.launch(Dispatchers.IO) {
                registerCommandsEventFlow
                    .first()
                    .adminPrivate(adminPrivateCommandExecutor)
            }
        },
        onReload = {
        },
        onDisable = {
            adminPrivateController.cancel()
        }
    )
}
