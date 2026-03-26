package ru.astrainteractive.aspekt.module.restrictions.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.restrictions.event.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.module.restrictions.model.RestrictionsConfiguration
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.util.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.util.withDefault

class RestrictionModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    val restrictionKrate = coreModule.yamlFormat
        .krateOf<RestrictionsConfiguration>(coreModule.dataFolder.resolve("restrictions.yml"))
        .withDefault(::RestrictionsConfiguration)
        .asCachedMutableKrate()
    private val restrictionsEvent: RestrictionsEvent by lazy {
        RestrictionsEvent(configKrate = restrictionKrate)
    }
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            restrictionsEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            restrictionsEvent.onDisable()
        }
    )
}
