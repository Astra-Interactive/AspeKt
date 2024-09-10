package ru.astrainteractive.aspekt.module.adminprivate.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate.AdminPrivateCommandRegistry
import ru.astrainteractive.aspekt.module.adminprivate.command.di.AdminPrivateCommandDependencies
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.event.AdminPrivateEvent
import ru.astrainteractive.aspekt.module.adminprivate.event.di.AdminPrivateDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kdi.Reloadable
import java.io.File

interface AdminPrivateModule {
    val lifecycle: Lifecycle
    val adminChunksYml: Reloadable<File>

    class Default(private val coreModule: CoreModule) : AdminPrivateModule {
        private val adminPrivateController: AdminPrivateController by lazy {
            val dependencies = AdminPrivateControllerDependencies.Default(
                coreModule = coreModule,
                adminPrivateModule = this
            )
            AdminPrivateController(dependencies)
        }

        private val adminPrivateCommandRegistry = AdminPrivateCommandRegistry(
            dependencies = AdminPrivateCommandDependencies.Default(
                coreModule = coreModule,
                adminPrivateController = adminPrivateController
            )
        )

        override val adminChunksYml: Reloadable<File> = Reloadable {
            coreModule.plugin.value.dataFolder.resolve("adminchunks.yml")
        }

        private val adminPrivateEvent by lazy {
            val adminPrivateDependencies: AdminPrivateDependencies = AdminPrivateDependencies.Default(
                coreModule = coreModule,
                adminPrivateController = adminPrivateController
            )
            AdminPrivateEvent(adminPrivateDependencies)
        }

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    adminPrivateCommandRegistry.register()
                    adminPrivateEvent.onEnable(coreModule.plugin.value)
                    adminPrivateController.reloadKrate()
                },
                onReload = {
                    adminPrivateController.reloadKrate()
                },
                onDisable = {
                    adminPrivateEvent.onDisable()
                }
            )
        }
    }
}
