package ru.astrainteractive.aspekt.module.auth.api.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.auth.api.api.AuthStateRepository
import ru.astrainteractive.aspekt.module.auth.api.memory.InMemoryAuthStateRepository
import ru.astrainteractive.aspekt.module.auth.api.model.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.parseOrWriteIntoDefault
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.io.File

class AuthApiModule(
    val authRepository: AuthRepository,
    ioScope: CoroutineScope,
    dataFolder: File,
    stringFormat: StringFormat
) {
    private val moduleIoScope = CoroutineScope(ioScope.coroutineContext + SupervisorJob())

    val authStateRepository: AuthStateRepository = InMemoryAuthStateRepository(
        authRepository = authRepository,
        scope = moduleIoScope
    )
    val translationKrate = DefaultMutableKrate(
        factory = ::AuthTranslation,
        loader = {
            stringFormat.parseOrWriteIntoDefault(
                file = dataFolder.resolve("translation.yml"),
                default = ::AuthTranslation
            )
        }
    ).asCachedKrate()

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onReload = {
            translationKrate.getValue()
        },
        onDisable = {
            moduleIoScope.cancel()
        }
    )

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("api.yml")
    }
}
