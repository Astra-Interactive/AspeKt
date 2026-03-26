package ru.astrainteractive.aspekt.module.restrictions.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.restrictions.event.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.module.restrictions.model.RestrictionsConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.util.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.util.withDefault

class RestrictionModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val restrictionConfigKrate = coreModule.yamlFormat
        .krateOf<RestrictionsConfiguration>(coreModule.dataFolder.resolve("restrictions.yml"))
        .withDefault(::RestrictionsConfiguration)
        .asCachedMutableKrate()

    private val restrictionsEvent: RestrictionsEvent by lazy {
        RestrictionsEvent(configKrate = restrictionConfigKrate)
    }
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            restrictionsEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            restrictionsEvent.onDisable()
        },
        onReload = {
            restrictionConfigKrate.getValue()
        }
    )
}
