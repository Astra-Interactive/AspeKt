package ru.astrainteractive.aspekt.gui.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.gui.RouterImpl
import ru.astrainteractive.aspekt.module.menu.di.MenuModule
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface GuiModule {
    val router: Router

    class Default(coreModule: CoreModule, menuModule: MenuModule) : GuiModule {
        override val router: Router by Single {
            RouterImpl(
                scope = coreModule.scope.value,
                dispatchers = coreModule.dispatchers.value,
                translationContext = coreModule.translationContext,
                menuModule = menuModule
            )
        }
    }
}
