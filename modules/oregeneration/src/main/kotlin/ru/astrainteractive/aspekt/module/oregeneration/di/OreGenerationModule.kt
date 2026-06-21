package ru.astrainteractive.aspekt.module.oregeneration.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.oregeneration.event.OreGenerationEvent
import ru.astrainteractive.aspekt.module.oregeneration.mapping.OreHostMaterialMapper
import ru.astrainteractive.aspekt.module.oregeneration.model.OreGenerationConfiguration
import ru.astrainteractive.aspekt.module.oregeneration.populator.OreGenerationBlockPopulator
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate

class OreGenerationModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val oreGenerationConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = coreModule.dataFolder.resolve("ore-generation.yml"),
            factory = ::OreGenerationConfiguration
        )
        .asCachedMutableKrate()

    private val oreGenerationBlockPopulator = OreGenerationBlockPopulator(
        configKrate = oreGenerationConfigKrate,
        oreHostMaterialMapper = OreHostMaterialMapper()
    )

    private val oreGenerationEvent: OreGenerationEvent by lazy {
        OreGenerationEvent(
            server = bukkitCoreModule.plugin.server,
            populator = oreGenerationBlockPopulator
        )
    }

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            oreGenerationEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            oreGenerationEvent.onDisable()
        },
        onReload = {
            oreGenerationConfigKrate.getValue()
        }
    )
}
