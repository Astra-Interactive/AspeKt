package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.di.ControllersModule
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.event.sort.SortController
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class ControllersModuleImpl(
    rootModule: RootModule
) : ControllersModule {
    private val eventsModule by rootModule.eventsModule

    override val sitController: SitController by Single {
        SitController(eventsModule)
    }
    override val sortControllers: SortController by Single {
        SortController()
    }
    override val adminPrivateController: AdminPrivateController by Single {
        AdminPrivateController(rootModule.adminPrivateModule)
    }
}
