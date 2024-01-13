package ru.astrainteractive.aspekt.gui.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.gui.RouterImpl
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface GuiModule {
    val router: Router

    class Default(coreModule: CoreModule) : GuiModule {
        override val router: Router by Single {
            RouterImpl(
                scope = coreModule.scope.value,
                dispatchers = coreModule.dispatchers.value,
                kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value,
            )
        }
    }
}
