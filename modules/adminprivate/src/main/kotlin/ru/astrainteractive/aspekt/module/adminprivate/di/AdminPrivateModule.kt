package ru.astrainteractive.aspekt.module.adminprivate.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate.AdminPrivateCommandRegister
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.event.AdminPrivateEvent
import ru.astrainteractive.aspekt.module.adminprivate.event.di.AdminPrivateDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface AdminPrivateModule {
    val adminPrivateLifecycleFactory: Factory<Lifecycle>

    class Default(coreModule: CoreModule) : AdminPrivateModule {
        private val adminPrivateController: AdminPrivateController by Single {
            val dependencies = AdminPrivateControllerDependencies.Default(coreModule)
            AdminPrivateController(dependencies)
        }

        private val adminPrivateCommandRegistry = AdminPrivateCommandRegister(
            plugin = coreModule.plugin.value,
            adminPrivateController = adminPrivateController,
            scope = coreModule.scope.value,
            translation = coreModule.translation.value,
            dispatchers = coreModule.dispatchers.value,
            kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value
        )

        private val adminPrivateEventFactory = Factory {
            val adminPrivateDependencies: AdminPrivateDependencies = AdminPrivateDependencies.Default(
                coreModule = coreModule,
                adminPrivateController = adminPrivateController
            )
            AdminPrivateEvent(adminPrivateDependencies)
        }

        override val adminPrivateLifecycleFactory: Factory<Lifecycle> = Factory {
            Lifecycle.Lambda(
                onEnable = {
                    adminPrivateCommandRegistry.register()
                    adminPrivateEventFactory.create()
                    adminPrivateController.reloadKrate()
                },
                onReload = {
                    adminPrivateController.reloadKrate()
                }
            )
        }
    }
}
