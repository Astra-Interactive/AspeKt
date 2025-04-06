package ru.astrainteractive.aspekt.module.adminprivate.controller.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepository
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepositoryImpl
import java.io.File

interface AdminPrivateControllerDependencies {
    val repository: AdminPrivateRepository

    class Default(
        coreModule: CoreModule,
        adminChunksFile: File
    ) : AdminPrivateControllerDependencies {
        override val repository: AdminPrivateRepository by lazy {
            AdminPrivateRepositoryImpl(
                file = adminChunksFile,
                stringFormat = coreModule.yamlFormat
            )
        }
    }
}
