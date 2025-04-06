package ru.astrainteractive.aspekt.module.sit.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.sit.command.SitCommandDependencies
import ru.astrainteractive.aspekt.module.sit.command.sit
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.aspekt.module.sit.event.sit.SitEvent
import ru.astrainteractive.aspekt.module.sit.event.sit.di.SitDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface SitModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : SitModule {
        private val sitController: SitController = SitController(
            configuration = coreModule.pluginConfig,
            translation = coreModule.translation,
            kyoriComponentSerializer = coreModule.kyoriComponentSerializer.cachedValue
        )

        private val sitEvent: SitEvent = SitEvent(
            dependencies = SitDependencies.Default(
                coreModule = coreModule,
                bukkitCoreModule = bukkitCoreModule,
                sitController = sitController
            )
        )
        private val sitCommandDependencies = object : SitCommandDependencies {
            override val plugin: JavaPlugin = bukkitCoreModule.plugin
            override val sitController: SitController = this@Default.sitController
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onDisable = {
                sitController.onDisable()
                sitEvent.onDisable()
            },
            onReload = { sitController.onDisable() },
            onEnable = {
                sitEvent.onEnable(bukkitCoreModule.plugin)
                sitCommandDependencies.sit()
            }
        )
    }
}
