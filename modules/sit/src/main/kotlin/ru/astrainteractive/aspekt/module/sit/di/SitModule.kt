package ru.astrainteractive.aspekt.module.sit.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.sit.command.SitCommandDependencies
import ru.astrainteractive.aspekt.module.sit.command.sit
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.aspekt.module.sit.event.sit.SitEvent
import ru.astrainteractive.aspekt.module.sit.event.sit.di.SitDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface SitModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : SitModule {
        private val sitController: SitController = SitController(
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
        private val sitCommandDependencies = object : SitCommandDependencies {
            override val plugin: JavaPlugin = coreModule.plugin
            override val sitController: SitController = this@Default.sitController
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onDisable = {
                sitController.onDisable()
                sitEvent.onDisable()
            },
            onReload = { sitController.onDisable() },
            onEnable = {
                sitEvent.onEnable(coreModule.plugin)
                sitCommandDependencies.sit()
            }
        )
    }
}
