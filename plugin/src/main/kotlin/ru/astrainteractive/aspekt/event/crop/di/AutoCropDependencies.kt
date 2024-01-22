package ru.astrainteractive.aspekt.event.crop.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.crop.domain.HoeDamager
import ru.astrainteractive.aspekt.event.crop.domain.HoeDamagerImpl
import ru.astrainteractive.aspekt.event.crop.domain.HoeRadiusFactory
import ru.astrainteractive.aspekt.event.crop.domain.HoeRadiusFactoryImpl
import ru.astrainteractive.aspekt.event.crop.domain.RelativeBlockProvider
import ru.astrainteractive.aspekt.event.crop.mapping.CropMaterialMapper
import ru.astrainteractive.aspekt.event.crop.mapping.CropMaterialMapperImpl
import ru.astrainteractive.aspekt.event.crop.presentation.CropDupeController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface AutoCropDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val configuration: PluginConfiguration
    val cropDupeController: CropDupeController
    val cropMaterialMapper: CropMaterialMapper
    val hoeRadiusFactory: HoeRadiusFactory
    val hoeDamager: HoeDamager
    val relativeBlockProviderFactory: Factory<RelativeBlockProvider>

    class Default(coreModule: CoreModule) : AutoCropDependencies {
        override val eventListener: EventListener by Provider {
            coreModule.eventListener.value
        }
        override val plugin: JavaPlugin by Provider {
            coreModule.plugin.value
        }
        override val configuration: PluginConfiguration by Provider {
            coreModule.pluginConfig.value
        }
        override val cropDupeController: CropDupeController by Single {
            CropDupeController()
        }
        override val cropMaterialMapper: CropMaterialMapper by lazy {
            CropMaterialMapperImpl()
        }
        override val hoeRadiusFactory: HoeRadiusFactory by lazy {
            HoeRadiusFactoryImpl()
        }
        override val relativeBlockProviderFactory: Factory<RelativeBlockProvider> = Factory {
            RelativeBlockProvider()
        }
        override val hoeDamager: HoeDamager by lazy {
            HoeDamagerImpl()
        }
    }
}
