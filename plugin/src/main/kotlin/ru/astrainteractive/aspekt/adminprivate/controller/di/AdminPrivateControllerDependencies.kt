package ru.astrainteractive.aspekt.adminprivate.controller.di

import ru.astrainteractive.aspekt.adminprivate.data.AdminPrivateRepository
import ru.astrainteractive.aspekt.adminprivate.data.AdminPrivateRepositoryImpl
import ru.astrainteractive.astralibs.filemanager.FileManager
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface AdminPrivateControllerDependencies : Module {
    val repository: AdminPrivateRepository
    val dispatchers: KotlinDispatchers

    class Default(
        adminChunksYml: Provider<FileManager>,
        dispatchers: Provider<KotlinDispatchers>
    ) : AdminPrivateControllerDependencies {
        override val repository: AdminPrivateRepository by Provider {
            AdminPrivateRepositoryImpl(
                fileManager = adminChunksYml.provide(),
                dispatchers = dispatchers.provide()
            )
        }
        override val dispatchers: KotlinDispatchers by dispatchers
    }
}
