package ru.astrainteractive.aspekt.module.autocrop.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.autocrop.AutoCropEvent
import ru.astrainteractive.aspekt.module.autocrop.domain.HoeDamagerImpl
import ru.astrainteractive.aspekt.module.autocrop.domain.HoeRadiusFactoryImpl
import ru.astrainteractive.aspekt.module.autocrop.domain.RelativeBlockProvider
import ru.astrainteractive.aspekt.module.autocrop.mapping.CropMaterialMapperImpl
import ru.astrainteractive.aspekt.module.autocrop.mapping.CropSeedMaterialMapperImpl
import ru.astrainteractive.aspekt.module.autocrop.presentation.CropDupeController
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class AutoCropModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val autoCropEvent: AutoCropEvent = AutoCropEvent(
        pluginConfig = coreModule.configKrate,
        hoeDamager = HoeDamagerImpl(),
        cropMaterialMapper = CropMaterialMapperImpl(),
        cropSeedMaterialMapper = CropSeedMaterialMapperImpl(),
        cropDupeController = CropDupeController(),
        hoeRadiusFactory = HoeRadiusFactoryImpl(),
        createRelativeBlockProvider = { RelativeBlockProvider() }
    )

    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                autoCropEvent.onEnable(bukkitCoreModule.plugin)
            },
            onDisable = {
                autoCropEvent.onDisable()
            }
        )
    }
}
