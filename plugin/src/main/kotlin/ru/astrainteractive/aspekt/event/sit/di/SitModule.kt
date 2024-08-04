package ru.astrainteractive.aspekt.event.sit.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.event.sit.SitEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface SitModule : Lifecycle {
    val sitController: SitController
    val sitEvent: SitEvent

    class Default(coreModule: CoreModule) : SitModule {
        override val sitController: SitController by lazy {
            SitController(
                configuration = { coreModule.pluginConfig.value },
                translation = { coreModule.translation.value },
                kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value
            )
        }

        override val sitEvent: SitEvent by lazy {
            val dependencies = SitDependencies.Default(
                coreModule,
                this
            )
            SitEvent(dependencies)
        }

        override fun onDisable() {
            sitController.onDisable()
        }

        override fun onReload() {
            sitController.onDisable()
        }
    }
}
