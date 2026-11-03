package ru.astrainteractive.aspekt.module.auth.api.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.auth.api.api.AuthStateRepository
import ru.astrainteractive.aspekt.module.auth.api.command.LoginCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.api.command.RegisterCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.api.command.UnregisterCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.api.model.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

@Suppress("LongParameterList")
class AuthCommandModule(
    authRepository: AuthRepository,
    authStateRepository: AuthStateRepository,
    translationKrate: CachedKrate<AuthTranslation>,
    ioScope: CoroutineScope,
    platformServer: PlatformServer,
    multiplatformCommand: MultiplatformCommand,
    commandExceptionHandler: CommandExceptionHandler,
    unconfinedScope: CoroutineScope,
    commandRegistrarContext: CommandRegistrarContext
) {
    private val moduleUnconfinedScope = CoroutineScope(unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = buildList {
        LoginCommandRegistrar(
            ioScope = ioScope,
            authRepository = authRepository,
            authStateRepository = authStateRepository,
            translationKrate = translationKrate,
            multiplatformCommand = multiplatformCommand,
            commandExceptionHandler = commandExceptionHandler
        ).createNodes().run(::addAll)
        RegisterCommandRegistrar(
            ioScope = ioScope,
            authRepository = authRepository,
            authStateRepository = authStateRepository,
            translationKrate = translationKrate,
            multiplatformCommand = multiplatformCommand,
            commandExceptionHandler = commandExceptionHandler
        ).createNodes().run(::addAll)
        UnregisterCommandRegistrar(
            ioScope = ioScope,
            authRepository = authRepository,
            authStateRepository = authStateRepository,
            platformServer = platformServer,
            translationKrate = translationKrate,
            multiplatformCommand = multiplatformCommand,
            commandExceptionHandler = commandExceptionHandler
        ).createNodes().run(::addAll)
    }

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
