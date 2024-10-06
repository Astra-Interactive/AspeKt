package ru.astrainteractive.aspekt.gui.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.gui.RouterImpl

interface GuiModule {
    val router: Router

    class Default(coreModule: CoreModule) : GuiModule {
        override val router: Router = RouterImpl(
            scope = coreModule.scope,
            dispatchers = coreModule.dispatchers,
            kyoriComponentSerializerKrate = coreModule.kyoriComponentSerializer,
        )
    }
}
