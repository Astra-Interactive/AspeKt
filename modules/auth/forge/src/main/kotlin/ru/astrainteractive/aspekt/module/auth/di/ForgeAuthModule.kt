package ru.astrainteractive.aspekt.module.auth.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.auth.api.di.AuthApiModule
import ru.astrainteractive.aspekt.module.auth.command.di.AuthCommandModule
import ru.astrainteractive.aspekt.module.auth.event.ForgeAuthEvent
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
        authDao = authApiModule.authDao,
        authorizedApi = authApiModule.authorizedApi,
        translationKrate = authApiModule.translationKrate,
        ioScope = coreModule.ioScope,
        kyoriKrate = coreModule.kyoriKrate,
        platformServer = coreModule.platformServer,
        multiplatformCommand = coreModule.multiplatformCommand,
        commandRegistrarContext = commandRegistrarContext
    )

    @Suppress("UnusedPrivateProperty")
    private val forgeAuthEvent = ForgeAuthEvent(
        authorizedApi = authApiModule.authorizedApi,
        kyoriKrate = coreModule.kyoriKrate,
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
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("auth.yml")
    }
}
