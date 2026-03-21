package ru.astrainteractive.aspekt.module.auth.command.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.di.AuthApiModule
import ru.astrainteractive.aspekt.module.auth.command.login.LoginCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.command.register.RegisterCommandRegistrar
import ru.astrainteractive.aspekt.module.auth.command.unregister.UnregisterCommandRegistrar
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import kotlin.collections.onEach

/**
 * Aggregates and registers Brigadier command nodes for Auth (Forge) module.
 */
class AuthCommandModule(
    private val authDao: AuthDao,
    private val authorizedApi: AuthorizedApi,
    private val coreModule: CoreModule,
    private val authApiModule: AuthApiModule,
    private val forgeCommandRegistrarContext: NeoForgeCommandRegistrarContext
) {
    private val nodes = buildList {
        LoginCommandRegistrar(
            ioScope = coreModule.ioScope,
            authDao = authDao,
            authorizedApi = authorizedApi,
            kyoriKrate = coreModule.kyoriKrate,
            translationKrate = authApiModule.translationKrate,
            multiplatformCommand = coreModule.multiplatformCommand
        ).createNode().run(::add)
        RegisterCommandRegistrar(
            ioScope = coreModule.ioScope,
            authDao = authDao,
            authorizedApi = authorizedApi,
            kyoriKrate = coreModule.kyoriKrate,
            translationKrate = authApiModule.translationKrate,
            multiplatformCommand = coreModule.multiplatformCommand
        ).createNode().run(::add)
        UnregisterCommandRegistrar(
            ioScope = coreModule.ioScope,
            authDao = authDao,
            authorizedApi = authorizedApi,
            kyoriKrate = coreModule.kyoriKrate,
            translationKrate = authApiModule.translationKrate,
            multiplatformCommand = coreModule.multiplatformCommand
        ).createNode().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(forgeCommandRegistrarContext::registerWhenReady)
        }
    )
}
