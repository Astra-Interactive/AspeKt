package ru.astrainteractive.aspekt.module.auth.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.core.command.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.command.LoginCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.command.RegisterCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.command.UnregisterCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import ru.astrainteractive.aspekt.module.auth.model.AuthTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

@Suppress("LongParameterList")
class AuthCommandModule(
    authRepository: AuthRepository,
    authorizedApi: AuthorizedApi,
    translationKrate: CachedKrate<AuthTranslation>,
    ioScope: CoroutineScope,
    platformServer: PlatformServer,
    multiplatformCommand: MultiplatformCommand,
    commandExceptionHandler: CommandExceptionHandler,
    unconfinedScope: CoroutineScope,
    private val commandRegistrarContext: CommandRegistrarContext
) {
    private val moduleUnconfinedScope = CoroutineScope(unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = buildList {
        LoginCommandRegistrar(
            ioScope = ioScope,
            authRepository = authRepository,
            authorizedApi = authorizedApi,
            translationKrate = translationKrate,
            multiplatformCommand = multiplatformCommand,
            commandExceptionHandler = commandExceptionHandler
        ).createNodes().run(::addAll)
        RegisterCommandRegistrar(
            ioScope = ioScope,
            authRepository = authRepository,
            authorizedApi = authorizedApi,
            translationKrate = translationKrate,
            multiplatformCommand = multiplatformCommand,
            commandExceptionHandler = commandExceptionHandler
        ).createNodes().run(::addAll)
        UnregisterCommandRegistrar(
            ioScope = ioScope,
            authRepository = authRepository,
            authorizedApi = authorizedApi,
            platformServer = platformServer,
            translationKrate = translationKrate,
            multiplatformCommand = multiplatformCommand,
            commandExceptionHandler = commandExceptionHandler
        ).createNodes().run(::addAll)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
