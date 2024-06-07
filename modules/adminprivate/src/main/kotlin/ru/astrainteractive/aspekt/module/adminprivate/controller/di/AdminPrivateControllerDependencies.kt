package ru.astrainteractive.aspekt.module.adminprivate.controller.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepository
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepositoryImpl
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface AdminPrivateControllerDependencies {
    val repository: AdminPrivateRepository
    val dispatchers: KotlinDispatchers

    class Default(coreModule: CoreModule) : AdminPrivateControllerDependencies {
        override val repository: AdminPrivateRepository by Provider {
            AdminPrivateRepositoryImpl(
                fileManager = coreModule.adminChunksYml.value,
                dispatchers = coreModule.dispatchers.value
            )
        }
        override val dispatchers: KotlinDispatchers by coreModule.dispatchers
    }
}
