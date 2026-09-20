package ru.astrainteractive.aspekt.module.menu.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.command.invclose.InvCloseLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.menu.command.menu.MenuLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

/**
 * Aggregates and registers Brigadier command nodes for Menu module.
 */
internal class MenuCommandModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val menuRouter: () -> MenuRouter,
    private val menuModelsKrate: CachedKrate<List<MenuModel>>
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = listOf(
        MenuLiteralArgumentBuilder(
            translationKrate = coreModule.translationKrate,
            kyoriKrate = coreModule.kyoriKrate,
            menuRouter = menuRouter,
            menuModelsKrate = menuModelsKrate,
            multiplatformCommand = coreModule.multiplatformCommand
        ).create(),
        InvCloseLiteralArgumentBuilder(coreModule.multiplatformCommand)
            .create()
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
