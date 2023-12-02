package ru.astrainteractive.aspekt.adminprivate.di

import ru.astrainteractive.aspekt.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface AdminPrivateModule {
    val adminPrivateController: AdminPrivateController

    class Default(coreModule: CoreModule) : AdminPrivateModule {
        override val adminPrivateController: AdminPrivateController by Single {
            val dependencies = AdminPrivateControllerDependencies.Default(coreModule)
            AdminPrivateController(dependencies)
        }
    }
}
