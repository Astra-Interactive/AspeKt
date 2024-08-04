package ru.astrainteractive.aspekt.module.adminprivate.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate.AdminPrivateCommandRegister
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.event.AdminPrivateEvent
import ru.astrainteractive.aspekt.module.adminprivate.event.di.AdminPrivateDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface AdminPrivateModule {
    val lifecycle: Lifecycle

    class Default(private val coreModule: CoreModule) : AdminPrivateModule {
        private val adminPrivateController: AdminPrivateController by lazy {
            val dependencies = AdminPrivateControllerDependencies.Default(coreModule)
            AdminPrivateController(dependencies)
        }

        private val adminPrivateCommandRegistry = AdminPrivateCommandRegister(
            plugin = coreModule.plugin.value,
            adminPrivateController = adminPrivateController,
            scope = coreModule.scope,
            translation = coreModule.translation.value,
            dispatchers = coreModule.dispatchers,
            kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value
        )

        private fun createAdminPrivateEvent() {
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
                    createAdminPrivateEvent()
                    adminPrivateController.reloadKrate()
                },
                onReload = {
                    adminPrivateController.reloadKrate()
                }
            )
        }
    }
}
