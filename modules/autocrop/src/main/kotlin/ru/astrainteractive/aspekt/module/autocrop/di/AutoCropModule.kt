package ru.astrainteractive.aspekt.module.autocrop.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.autocrop.event.AutoCropEvent
import ru.astrainteractive.aspekt.module.autocrop.event.CropDupeController
import ru.astrainteractive.aspekt.module.autocrop.model.AutoCropConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import java.io.File

class AutoCropModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {

    private val autoCropConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = resolveConfigurationFile(coreModule.dataFolder),
            factory = ::AutoCropConfiguration
        )
        .asCachedMutableKrate()

    private val autoCropEvent: AutoCropEvent = AutoCropEvent(
        pluginConfig = autoCropConfigKrate,
        cropDupeController = CropDupeController()
    )

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                autoCropEvent.onEnable(bukkitCoreModule.plugin)
            },
            onDisable = {
                autoCropEvent.onDisable()
            },
            onReload = {
                autoCropConfigKrate.getValue()
            }
        )
    }

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("auto_crop.yml")
    }
}
