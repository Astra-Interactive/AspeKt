package ru.astrainteractive.aspekt.module.adminprivate.di

import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate.AdminPrivateCommandRegistry
import ru.astrainteractive.aspekt.module.adminprivate.command.di.AdminPrivateCommandDependencies
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.event.AdminPrivateEvent
import ru.astrainteractive.aspekt.module.adminprivate.event.di.AdminPrivateDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

interface AdminPrivateModule {
    val lifecycle: Lifecycle
    val adminChunksFile: File

    class Default(
        private val coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : AdminPrivateModule {
        private val adminPrivateController = AdminPrivateController(
            dependencies = AdminPrivateControllerDependencies.Default(
                coreModule = coreModule,
                adminPrivateModule = this
            )
        )

        private val adminPrivateCommandRegistry = AdminPrivateCommandRegistry(
            dependencies = AdminPrivateCommandDependencies.Default(
                coreModule = coreModule,
                adminPrivateController = adminPrivateController,
                bukkitCoreModule = bukkitCoreModule
            )
        )

        override val adminChunksFile: File = coreModule.dataFolder.resolve("adminchunks.yml")

        private val adminPrivateEvent = AdminPrivateEvent(
            dependencies = AdminPrivateDependencies.Default(
                coreModule = coreModule,
                adminPrivateController = adminPrivateController,
                bukkitCoreModule = bukkitCoreModule
            )
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                adminPrivateCommandRegistry.register()
                adminPrivateEvent.onEnable(bukkitCoreModule.plugin)
                adminPrivateController.reloadKrate()
            },
            onReload = {
                adminPrivateController.reloadKrate()
            },
            onDisable = {
                adminPrivateEvent.onDisable()
                adminPrivateController.cancel()
            }
        )
    }
}
