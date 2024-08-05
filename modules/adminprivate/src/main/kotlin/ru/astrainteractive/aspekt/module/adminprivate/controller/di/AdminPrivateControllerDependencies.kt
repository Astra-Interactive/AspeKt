package ru.astrainteractive.aspekt.module.adminprivate.controller.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepository
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepositoryImpl
import ru.astrainteractive.klibs.kdi.getValue

internal interface AdminPrivateControllerDependencies {
    val repository: AdminPrivateRepository

    class Default(coreModule: CoreModule) : AdminPrivateControllerDependencies {
        override val repository: AdminPrivateRepository by lazy {
            AdminPrivateRepositoryImpl(
                file = coreModule.adminChunksYml.value,
                dispatchers = coreModule.dispatchers,
                stringFormat = coreModule.yamlFormat
            )
        }
    }
}
