package ru.astrainteractive.aspekt.module.jail.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.jail.api.JailCache
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.command.JailLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.jail.usecase.FreeInmateUseCase
import ru.astrainteractive.aspekt.module.jail.usecase.TeleportInmateToJailUseCase
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import kotlin.time.Clock

internal class JailCommandModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule,
    jailRepository: JailRepository,
    jailCache: JailCache,
    freeInmateUseCase: FreeInmateUseCase,
    teleportInmateToJailUseCase: TeleportInmateToJailUseCase
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = buildList {
        JailLiteralArgumentBuilder(
            translationKrate = coreModule.translationKrate,
            scope = coreModule.ioScope,
            jailRepository = jailRepository,
            jailCache = jailCache,
            freeInmateUseCase = freeInmateUseCase,
            teleportInmateToJailUseCase = teleportInmateToJailUseCase,
            multiplatformCommand = coreModule.multiplatformCommand,
            platformServer = coreModule.platformServer,
            clock = Clock.System,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create().run(::add)
    }

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
