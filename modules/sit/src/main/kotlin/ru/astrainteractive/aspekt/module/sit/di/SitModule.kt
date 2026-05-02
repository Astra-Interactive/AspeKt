package ru.astrainteractive.aspekt.module.sit.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.sit.command.di.SitCommandModule
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.aspekt.module.sit.event.sit.SitEvent
import ru.astrainteractive.aspekt.module.sit.model.SitConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.api.withDefault

class SitModule(
    coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule
) {
    private val sitConfigKrate = coreModule.yamlFormat
        .krateOf<SitConfiguration>(coreModule.dataFolder.resolve("sit.yml"))
        .withDefault(::SitConfiguration)
        .asCachedMutableKrate()
    private val sitController: SitController = SitController(
        sitKrate = sitConfigKrate,
        translation = coreModule.translationKrate,
        kyoriComponentSerializer = coreModule.kyoriKrate.cachedValue
    )

    private val sitEvent: SitEvent = SitEvent(
        sitKrate = sitConfigKrate,
        sitController = sitController
    )

    private val sitCommandModule = SitCommandModule(
        bukkitCoreModule = bukkitCoreModule,
        sitController = sitController,
        coreModule = coreModule
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = {
            sitController.onDisable()
            sitEvent.onDisable()
        },
        onReload = {
            sitController.onDisable()
            sitConfigKrate.getValue()
        },
        onEnable = {
            sitEvent.onEnable(bukkitCoreModule.plugin)
            sitCommandModule.lifecycle.onEnable()
        }
    )
}
