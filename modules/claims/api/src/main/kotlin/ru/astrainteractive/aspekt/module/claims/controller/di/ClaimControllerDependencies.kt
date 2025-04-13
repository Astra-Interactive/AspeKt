package ru.astrainteractive.aspekt.module.claims.controller.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepositoryImpl
import java.io.File

interface ClaimControllerDependencies {
    val repository: ClaimsRepository

    class Default(
        folder: File,
        coreModule: CoreModule,
    ) : ClaimControllerDependencies {
        override val repository: ClaimsRepository by lazy {
            ClaimsRepositoryImpl(
                folder = folder,
                stringFormat = coreModule.yamlFormat
            )
        }
    }
}
