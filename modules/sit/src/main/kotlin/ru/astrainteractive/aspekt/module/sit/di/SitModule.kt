package ru.astrainteractive.aspekt.module.sit.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.sit.command.di.SitCommandModule
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.aspekt.module.sit.event.sit.SitEvent
import ru.astrainteractive.aspekt.module.sit.event.sit.di.SitDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class SitModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule
) {
    private val sitController: SitController = SitController(
        configuration = coreModule.configKrate,
        translation = coreModule.translation,
        kyoriComponentSerializer = coreModule.kyoriKrate.cachedValue
    )

    private val sitEvent: SitEvent = SitEvent(
        dependencies = SitDependencies.Default(
            coreModule = coreModule,
            bukkitCoreModule = bukkitCoreModule,
            sitController = sitController
        )
    )

    private val sitCommandModule = SitCommandModule(
        bukkitCoreModule = bukkitCoreModule,
        sitController = sitController
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = {
            sitController.onDisable()
            sitEvent.onDisable()
        },
        onReload = { sitController.onDisable() },
        onEnable = {
            sitEvent.onEnable(bukkitCoreModule.plugin)
            sitCommandModule.lifecycle.onEnable()
        }
    )
}
