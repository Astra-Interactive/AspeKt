package ru.astrainteractive.aspekt.module.menu.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.di.factory.MenuModelsFactory
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.module.menu.router.MenuRouterImpl
import ru.astrainteractive.aspekt.util.Lifecycle
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable

interface MenuModule : Lifecycle {
    val menuModels: Reloadable<List<MenuModel>>
    val menuRouter: Provider<MenuRouter>

    class Default(
        private val coreModule: CoreModule
    ) : MenuModule {

        override val menuModels: Reloadable<List<MenuModel>> = Reloadable {
            MenuModelsFactory(coreModule.plugin.value.dataFolder, YamlSerializer()).create()
        }

        override val menuRouter: Provider<MenuRouter> = Provider {
            MenuRouterImpl(coreModule)
        }

        override fun onReload() {
            menuModels.reload()
        }
    }
}
