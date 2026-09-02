package ru.astrainteractive.aspekt.module.restrictions.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.restrictions.event.RestrictionsEvent
import ru.astrainteractive.aspekt.module.restrictions.model.RestrictionsConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import java.io.File

class RestrictionModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val restrictionConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = getConfigurationFile(coreModule.dataFolder),
            factory = ::RestrictionsConfiguration
        )
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

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("restrictions.yml")
    }
}
