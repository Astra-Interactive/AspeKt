package ru.astrainteractive.aspekt.module.oregeneration.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.oregeneration.event.LootGenerationEvent
import ru.astrainteractive.aspekt.module.oregeneration.event.OreGenerationEvent
import ru.astrainteractive.aspekt.module.oregeneration.mapping.OreHostMaterialMapper
import ru.astrainteractive.aspekt.module.oregeneration.model.LootGenerationConfiguration
import ru.astrainteractive.aspekt.module.oregeneration.model.OreGenerationConfiguration
import ru.astrainteractive.aspekt.module.oregeneration.populator.OreGenerationBlockPopulator
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import java.io.File

class OreGenerationModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val oreGenerationConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = getConfigurationFile(coreModule.dataFolder),
            factory = ::OreGenerationConfiguration
        )
        .asCachedMutableKrate()

    private val oreGenerationBlockPopulator = OreGenerationBlockPopulator(
        configKrate = oreGenerationConfigKrate,
        oreHostMaterialMapper = OreHostMaterialMapper()
    )

    private val lootGenerationConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = coreModule.dataFolder.resolve("loot-generation.yml"),
            factory = ::LootGenerationConfiguration
        )
        .asCachedMutableKrate()

    private val lootGenerationEvent = LootGenerationEvent(configKrate = lootGenerationConfigKrate)
    private val oreGenerationEvent: OreGenerationEvent = OreGenerationEvent(
        server = bukkitCoreModule.plugin.server,
        populator = oreGenerationBlockPopulator
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            oreGenerationEvent.onEnable(bukkitCoreModule.plugin)
            lootGenerationEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            lootGenerationEvent.onDisable()
            oreGenerationEvent.onDisable()
        },
        onReload = {
            oreGenerationConfigKrate.getValue()
            lootGenerationConfigKrate.getValue()
        }
    )

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("ore-generation.yml")
    }
}
