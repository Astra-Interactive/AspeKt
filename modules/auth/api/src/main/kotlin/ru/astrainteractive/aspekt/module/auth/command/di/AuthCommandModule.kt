package ru.astrainteractive.aspekt.module.auth.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.di.AuthApiModule
import ru.astrainteractive.aspekt.module.auth.command.login.LoginCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.command.register.RegisterCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.command.unregister.UnregisterCommandRegistrar
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

class AuthCommandModule(
    private val authDao: AuthDao,
    private val authorizedApi: AuthorizedApi,
    private val authApiModule: AuthApiModule,
    private val ioScope: CoroutineScope,
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val platformServer: PlatformServer,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandRegistrarContext: CommandRegistrarContext
) {
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            LoginCommandRegistrar(
                ioScope = ioScope,
                authDao = authDao,
                authorizedApi = authorizedApi,
                kyoriKrate = kyoriKrate,
                translationKrate = authApiModule.translationKrate,
                multiplatformCommand = multiplatformCommand,
                registrarContext = commandRegistrarContext
            ).register()
            RegisterCommandRegistrar(
                ioScope = ioScope,
                authDao = authDao,
                authorizedApi = authorizedApi,
                kyoriKrate = kyoriKrate,
                translationKrate = authApiModule.translationKrate,
                multiplatformCommand = multiplatformCommand,
                registrarContext = commandRegistrarContext
            ).register()
            UnregisterCommandRegistrar(
                ioScope = ioScope,
                authDao = authDao,
                authorizedApi = authorizedApi,
                platformServer = platformServer,
                kyoriKrate = kyoriKrate,
                translationKrate = authApiModule.translationKrate,
                multiplatformCommand = multiplatformCommand,
                registrarContext = commandRegistrarContext
            ).register()
        }
    )
}
