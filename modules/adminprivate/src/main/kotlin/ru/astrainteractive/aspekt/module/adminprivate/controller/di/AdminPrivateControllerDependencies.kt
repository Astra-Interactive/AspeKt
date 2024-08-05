package ru.astrainteractive.aspekt.module.adminprivate.controller.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepository
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepositoryImpl
import ru.astrainteractive.aspekt.module.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.klibs.kdi.getValue

internal interface AdminPrivateControllerDependencies {
    val repository: AdminPrivateRepository

    class Default(
        coreModule: CoreModule,
        adminPrivateModule: AdminPrivateModule
    ) : AdminPrivateControllerDependencies {
        override val repository: AdminPrivateRepository by lazy {
            AdminPrivateRepositoryImpl(
                file = adminPrivateModule.adminChunksYml.value,
                dispatchers = coreModule.dispatchers,
                stringFormat = coreModule.yamlFormat
            )
        }
    }
}
