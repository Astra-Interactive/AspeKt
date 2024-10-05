package ru.astrainteractive.aspekt.event.sit.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.event.sit.SitEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface SitModule : Lifecycle {
    val sitController: SitController
    val sitEvent: SitEvent

    class Default(coreModule: CoreModule) : SitModule {
        override val sitController: SitController by lazy {
            SitController(
                configuration = coreModule.pluginConfig,
                translation = coreModule.translation,
                kyoriComponentSerializer = coreModule.kyoriComponentSerializer.cachedValue
            )
        }

        override val sitEvent: SitEvent by lazy {
            val dependencies = SitDependencies.Default(
                coreModule,
                this
            )
            SitEvent(dependencies)
        }

        override fun onEnable() {
            sitEvent
        }

        override fun onDisable() {
            sitController.onDisable()
        }

        override fun onReload() {
            sitController.onDisable()
        }
    }
}
