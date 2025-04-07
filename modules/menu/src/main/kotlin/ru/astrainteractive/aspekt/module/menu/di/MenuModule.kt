package ru.astrainteractive.aspekt.module.menu.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.command.MenuCommandFactory
import ru.astrainteractive.aspekt.module.menu.di.factory.MenuModelsFactory
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.module.menu.router.MenuRouterImpl
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.MutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate

interface MenuModule {
    val lifecycle: Lifecycle

    class Default(
        private val coreModule: CoreModule
    ) : MenuModule {
        private val menuModels: MutableKrate<List<MenuModel>> = DefaultMutableKrate(
            loader = {
                MenuModelsFactory(
                    coreModule.plugin.dataFolder,
                    coreModule.yamlFormat
                ).create()
            },
            factory = { emptyList() }
        )

        private val menuRouter: MenuRouter
            get() = MenuRouterImpl(coreModule)

        private val menuCommandFactory = MenuCommandFactory(
            plugin = coreModule.plugin,
            kyoriComponentSerializer = coreModule.kyoriComponentSerializer,
            menuModelProvider = menuModels,
            translationProvider = coreModule.translation,
            menuRouterProvider = { menuRouter }
        )

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    menuCommandFactory.create()
                },
                onReload = {
                    menuModels.loadAndGet()
                }
            )
        }
    }
}
