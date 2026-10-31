package ru.astrainteractive.aspekt.module.auth.forge.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.module.auth.api.command.di.AuthCommandModule
import ru.astrainteractive.aspekt.module.auth.api.di.AuthApiModule
import ru.astrainteractive.aspekt.module.auth.forge.event.ForgeAuthEvent
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class ForgeAuthModule(
    authApiModule: AuthApiModule,
    coreModule: CoreModule,
    commandRegistrarContext: CommandRegistrarContext
) {
    private val moduleMainScope = CoroutineScope(coreModule.mainScope.coroutineContext + SupervisorJob())

    private val authCommandModule = AuthCommandModule(
        authRepository = authApiModule.authRepository,
        authStateRepository = authApiModule.authStateRepository,
        translationKrate = authApiModule.translationKrate,
        ioScope = coreModule.ioScope,
        platformServer = coreModule.platformServer,
        multiplatformCommand = coreModule.multiplatformCommand,
        commandExceptionHandler = coreModule.commandExceptionHandler,
        unconfinedScope = coreModule.unconfinedScope,
        commandRegistrarContext = commandRegistrarContext
    )

    @Suppress("UnusedPrivateProperty")
    private val forgeAuthEvent = ForgeAuthEvent(
        authStateRepository = authApiModule.authStateRepository,
        translationKrate = authApiModule.translationKrate,
        mainScope = moduleMainScope
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            authCommandModule.lifecycle.onEnable()
        },
        onDisable = {
            authCommandModule.lifecycle.onDisable()
            moduleMainScope.cancel()
        }
    )

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("auth.yml")
    }
}
