package ru.astrainteractive.aspekt.module.treecapitator.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.treecapitator.event.tc.TCEvent
import ru.astrainteractive.aspekt.module.treecapitator.model.TreeCapitatorConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate

class TreeCapitatorModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val tcConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = coreModule.dataFolder.resolve("config.yml"),
            factory = ::TreeCapitatorConfiguration
        )
        .asCachedMutableKrate()

    private val tcEvent = TCEvent(
        tcConfigKrate = tcConfigKrate,
        ioScope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers,
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            tcEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            tcEvent.onDisable()
        },
        onReload = {
            tcConfigKrate.getValue()
        }
    )
}
