package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.adminprivate.controller.di.AdminPrivateControllerModule
import ru.astrainteractive.aspekt.adminprivate.data.AdminPrivateRepository
import ru.astrainteractive.aspekt.adminprivate.data.AdminPrivateRepositoryImpl
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.astralibs.async.KotlinDispatchers
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class AdminPrivateControllerModuleImpl(rootModule: RootModule) : AdminPrivateControllerModule {
    override val repository: AdminPrivateRepository by Provider {
        AdminPrivateRepositoryImpl(
            fileManager = rootModule.adminChunksYml.value,
            dispatchers = rootModule.dispatchers.value
        )
    }
    override val dispatchers: KotlinDispatchers by rootModule.dispatchers
}
