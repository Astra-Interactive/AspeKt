package ru.astrainteractive.aspekt.event.sit.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.event.sit.SitEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface SitModule {
    val lifecycle: Lifecycle

    val sitController: SitController

    class Default(coreModule: CoreModule) : SitModule {
        override val sitController: SitController = SitController(
            configuration = coreModule.pluginConfig,
            translation = coreModule.translation,
            kyoriComponentSerializer = coreModule.kyoriComponentSerializer.cachedValue
        )

        private val sitEvent: SitEvent = SitEvent(
            dependencies = SitDependencies.Default(
                coreModule = coreModule,
                sitController = sitController
            )
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onDisable = {
                sitController.onDisable()
                sitEvent.onDisable()
            },
            onReload = { sitController.onDisable() },
            onEnable = {
                sitEvent.onEnable(coreModule.plugin)
            }
        )
    }
}
