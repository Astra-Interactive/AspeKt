package ru.astrainteractive.aspekt.module.menu.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.command.di.MenuCommandModule
import ru.astrainteractive.aspekt.module.menu.di.factory.MenuModelsFactory
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.module.menu.router.MenuRouterImpl
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate

class MenuModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule
) {
    private val menuModels = DefaultMutableKrate(
        loader = {
            MenuModelsFactory(
                bukkitCoreModule.plugin.dataFolder,
                coreModule.yamlFormat
            ).create()
        },
        factory = { emptyList() }
    ).asCachedKrate()

    private val menuRouter: MenuRouter = MenuRouterImpl(coreModule, bukkitCoreModule)

    private val menuCommandModule = MenuCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        menuRouter = { menuRouter },
        menuModels = menuModels.cachedValue
    )

    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                menuCommandModule.lifecycle.onEnable()
            },
            onReload = {
                menuModels.getValue()
            }
        )
    }
}
