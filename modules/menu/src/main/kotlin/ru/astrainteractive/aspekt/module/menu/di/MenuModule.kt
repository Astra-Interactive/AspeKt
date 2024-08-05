package ru.astrainteractive.aspekt.module.menu.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.command.MenuCommandFactory
import ru.astrainteractive.aspekt.module.menu.di.factory.MenuModelsFactory
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.module.menu.router.MenuRouterImpl
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable

interface MenuModule {
    val lifecycle: Lifecycle

    class Default(
        private val coreModule: CoreModule
    ) : MenuModule {
        private val menuModels: Reloadable<List<MenuModel>> = Reloadable {
            MenuModelsFactory(
                coreModule.plugin.value.dataFolder,
                coreModule.yamlFormat
            ).create()
        }

        private val menuRouter: Provider<MenuRouter> = Provider {
            MenuRouterImpl(coreModule)
        }

        private val menuCommandFactory = MenuCommandFactory(
            plugin = coreModule.plugin.value,
            kyoriComponentSerializer = coreModule.kyoriComponentSerializer,
            menuModelProvider = { menuModels.value },
            translationProvider = { coreModule.translation.value },
            menuRouter = { menuRouter.provide() }
        )

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    menuCommandFactory.create()
                },
                onReload = {
                    menuModels.reload()
                }
            )
        }
    }
}
