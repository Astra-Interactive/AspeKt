package ru.astrainteractive.aspekt.module.auth.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.command.login.LoginCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.command.register.RegisterCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.command.unregister.UnregisterCommandRegistrar
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

@Suppress("LongParameterList")
class AuthCommandModule(
    authDao: AuthDao,
    authorizedApi: AuthorizedApi,
    translationKrate: CachedKrate<AuthTranslation>,
    ioScope: CoroutineScope,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    platformServer: PlatformServer,
    multiplatformCommand: MultiplatformCommand,
    unconfinedScope: CoroutineScope,
    private val commandRegistrarContext: CommandRegistrarContext
) {
    private val moduleUnconfinedScope = CoroutineScope(unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = buildList {
        LoginCommandRegistrar(
            ioScope = ioScope,
            authDao = authDao,
            authorizedApi = authorizedApi,
            kyoriKrate = kyoriKrate,
            translationKrate = translationKrate,
            multiplatformCommand = multiplatformCommand
        ).createNodes().run(::addAll)
        RegisterCommandRegistrar(
            ioScope = ioScope,
            authDao = authDao,
            authorizedApi = authorizedApi,
            kyoriKrate = kyoriKrate,
            translationKrate = translationKrate,
            multiplatformCommand = multiplatformCommand
        ).createNodes().run(::addAll)
        UnregisterCommandRegistrar(
            ioScope = ioScope,
            authDao = authDao,
            authorizedApi = authorizedApi,
            platformServer = platformServer,
            kyoriKrate = kyoriKrate,
            translationKrate = translationKrate,
            multiplatformCommand = multiplatformCommand
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
