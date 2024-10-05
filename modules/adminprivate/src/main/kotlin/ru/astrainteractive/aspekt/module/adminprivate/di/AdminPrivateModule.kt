package ru.astrainteractive.aspekt.module.adminprivate.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate.AdminPrivateCommandRegistry
import ru.astrainteractive.aspekt.module.adminprivate.command.di.AdminPrivateCommandDependencies
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.event.AdminPrivateEvent
import ru.astrainteractive.aspekt.module.adminprivate.event.di.AdminPrivateDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.api.MutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.io.File

interface AdminPrivateModule {
    val lifecycle: Lifecycle
    val adminChunksYml: Krate<File>

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

        override val adminChunksYml: MutableKrate<File> = DefaultMutableKrate(
            factory = {
                val file = coreModule.plugin.dataFolder.resolve("adminchunks.yml")
                if (!file.exists()) file.createNewFile()
                file
            },
            loader = { null }
        )

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
                    adminPrivateEvent.onEnable(coreModule.plugin)
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
