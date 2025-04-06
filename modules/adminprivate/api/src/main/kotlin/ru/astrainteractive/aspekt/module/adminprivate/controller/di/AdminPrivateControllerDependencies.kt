package ru.astrainteractive.aspekt.module.adminprivate.controller.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.adminprivate.data.ClaimsRepositoryImpl
import java.io.File

interface AdminPrivateControllerDependencies {
    val repository: ClaimsRepository

    class Default(
        folder: File,
        coreModule: CoreModule,
    ) : AdminPrivateControllerDependencies {
        override val repository: ClaimsRepository by lazy {
            ClaimsRepositoryImpl(
                folder = folder,
                stringFormat = coreModule.yamlFormat
            )
        }
    }
}
