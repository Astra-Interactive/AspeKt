package ru.astrainteractive.aspekt.module.auth.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.module.auth.api.di.AuthApiModule
import ru.astrainteractive.aspekt.module.auth.command.loginCommand
import ru.astrainteractive.aspekt.module.auth.command.registerCommand
import ru.astrainteractive.aspekt.module.auth.command.unregisterCommand
import ru.astrainteractive.aspekt.module.auth.event.ForgeAuthEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

class ForgeAuthModule(
    private val scope: CoroutineScope,
    private val authApiModule: AuthApiModule,
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    registerCommandsEventFlow: Flow<RegisterCommandsEvent>,
) {
    @Suppress("UnusedPrivateProperty")
    private val commandsJob = registerCommandsEventFlow
        .onEach { registerCommandsEvent ->
            registerCommandsEvent.loginCommand(
                scope = scope,
                authDao = authApiModule.authDao,
                authorizedApi = authApiModule.authorizedApi,
                kyoriKrate = kyoriKrate,
                translationKrate = authApiModule.translationKrate
            )
            registerCommandsEvent.registerCommand(
                scope = scope,
                authDao = authApiModule.authDao,
                authorizedApi = authApiModule.authorizedApi,
                kyoriKrate = kyoriKrate,
                translationKrate = authApiModule.translationKrate
            )
            registerCommandsEvent.unregisterCommand(
                scope = scope,
                authDao = authApiModule.authDao,
                authorizedApi = authApiModule.authorizedApi,
                kyoriKrate = kyoriKrate,
                translationKrate = authApiModule.translationKrate
            )
        }.launchIn(scope)

    @Suppress("UnusedPrivateProperty")
    private val forgeAuthEvent = ForgeAuthEvent(
        authorizedApi = authApiModule.authorizedApi,
        kyoriKrate = kyoriKrate,
        translationKrate = authApiModule.translationKrate
    )

    val lifecycle = Lifecycle.Lambda()
}
