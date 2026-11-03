package ru.astrainteractive.aspekt.module.menu.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.core.di.BukkitCoreModule
import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.command.InvCloseLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.menu.command.MenuLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.menu.model.MenuConfiguration
import ru.astrainteractive.aspekt.module.menu.router.api.MenuRouter
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

internal class MenuCommandModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule,
    menuRouter: () -> MenuRouter,
    menuModelsKrate: CachedKrate<List<MenuConfiguration>>
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = listOf(
        MenuLiteralArgumentBuilder(
            translationKrate = coreModule.translationKrate,
            menuRouter = menuRouter,
            menuModelsKrate = menuModelsKrate,
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create(),
        InvCloseLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create()
    )

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
