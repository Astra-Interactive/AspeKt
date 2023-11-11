package ru.astrainteractive.aspekt.gui.di

import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.gui.RouterImpl
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface GuiModule {
    val router: Router

    class Default(rootModule: RootModule) : GuiModule {
        override val router: Router by Single {
            RouterImpl(
                scope = rootModule.scope.value,
                dispatchers = rootModule.dispatchers.value,
                translationContext = rootModule.translationContext,
                economyProvider = rootModule.economyProvider.value,
                translation = rootModule.translation.value
            )
        }
    }
}
