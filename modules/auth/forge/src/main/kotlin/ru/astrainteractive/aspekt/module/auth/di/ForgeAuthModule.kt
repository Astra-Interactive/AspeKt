package ru.astrainteractive.aspekt.module.auth.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.auth.api.di.AuthApiModule
import ru.astrainteractive.aspekt.module.auth.command.di.AuthCommandModule
import ru.astrainteractive.aspekt.module.auth.event.ForgeAuthEvent
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class ForgeAuthModule(
    authApiModule: AuthApiModule,
    coreModule: CoreModule,
    commandRegistrarContext: NeoForgeCommandRegistrarContext
) {
    private val authCommandModule = AuthCommandModule(
        authDao = authApiModule.authDao,
        authorizedApi = authApiModule.authorizedApi,
        coreModule = coreModule,
        authApiModule = authApiModule,
        forgeCommandRegistrarContext = commandRegistrarContext
    )

    @Suppress("UnusedPrivateProperty")
    private val forgeAuthEvent = ForgeAuthEvent(
        authorizedApi = authApiModule.authorizedApi,
        kyoriKrate = coreModule.kyoriKrate,
        translationKrate = authApiModule.translationKrate,
        mainScope = coreModule.mainScope
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = { authCommandModule.lifecycle.onEnable() },
        onDisable = { authCommandModule.lifecycle.onDisable() }
    )
}
