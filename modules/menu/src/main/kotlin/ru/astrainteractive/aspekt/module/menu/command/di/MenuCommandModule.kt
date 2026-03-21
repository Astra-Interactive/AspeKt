package ru.astrainteractive.aspekt.module.menu.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.command.invclose.InvCloseLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.menu.command.menu.MenuLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for Menu module.
 */
internal class MenuCommandModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val menuRouter: () -> MenuRouter,
    private val menuModels: List<MenuModel>
) {
    private val nodes = listOf(
        MenuLiteralArgumentBuilder(
            translationKrate = coreModule.translation,
            kyoriKrate = coreModule.kyoriKrate,
            menuRouter = menuRouter,
            menuModels = menuModels,
            multiplatformCommand = coreModule.multiplatformCommand
        ).create(),
        InvCloseLiteralArgumentBuilder(coreModule.multiplatformCommand)
            .create()
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
