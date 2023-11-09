package ru.astrainteractive.aspekt.event.sit.di

import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.event.sit.SitEvent
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface SitModule {
    val sitController: SitController
    val sitEvent: SitEvent

    class Default(rootModule: RootModule) : SitModule {
        override val sitController: SitController by Single {
            SitController(
                configuration = { rootModule.pluginConfig.value },
                translation = { rootModule.translation.value },
                translationContext = rootModule.translationContext
            )
        }

        override val sitEvent: SitEvent by lazy {
            val dependencies = SitDependencies.Default(
                rootModule,
                sitController
            )
            SitEvent(dependencies)
        }

    }
}