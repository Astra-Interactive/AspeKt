package ru.astrainteractive.aspekt.module.autocrop.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.autocrop.domain.HoeDamager
import ru.astrainteractive.aspekt.module.autocrop.domain.HoeDamagerImpl
import ru.astrainteractive.aspekt.module.autocrop.domain.HoeRadiusFactory
import ru.astrainteractive.aspekt.module.autocrop.domain.HoeRadiusFactoryImpl
import ru.astrainteractive.aspekt.module.autocrop.domain.RelativeBlockProvider
import ru.astrainteractive.aspekt.module.autocrop.mapping.CropMaterialMapper
import ru.astrainteractive.aspekt.module.autocrop.mapping.CropMaterialMapperImpl
import ru.astrainteractive.aspekt.module.autocrop.presentation.CropDupeController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal interface AutoCropDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val configuration: PluginConfiguration
    val cropDupeController: CropDupeController
    val cropMaterialMapper: CropMaterialMapper
    val hoeRadiusFactory: HoeRadiusFactory
    val hoeDamager: HoeDamager
    fun createRelativeBlockProvider(): RelativeBlockProvider

    class Default(coreModule: CoreModule) : AutoCropDependencies {
        override val eventListener: EventListener = coreModule.eventListener
        override val plugin: JavaPlugin by Provider {
            coreModule.plugin.value
        }
        override val configuration: PluginConfiguration by Provider {
            coreModule.pluginConfig.value
        }
        override val cropDupeController: CropDupeController by lazy {
            CropDupeController()
        }
        override val cropMaterialMapper: CropMaterialMapper by lazy {
            CropMaterialMapperImpl()
        }
        override val hoeRadiusFactory: HoeRadiusFactory by lazy {
            HoeRadiusFactoryImpl()
        }

        override fun createRelativeBlockProvider(): RelativeBlockProvider {
            return RelativeBlockProvider()
        }

        override val hoeDamager: HoeDamager by lazy {
            HoeDamagerImpl()
        }
    }
}
