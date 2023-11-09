package ru.astrainteractive.aspekt.event.crop.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.crop.CropDupeController
import ru.astrainteractive.aspekt.event.restrictions.di.RestrictionsDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface AutoCropDependencies {
    val eventListener: EventListener
    val plugin: AspeKt
    val configuration: PluginConfiguration
    val cropDupeController: CropDupeController

    class Default(rootModule: RootModule) : AutoCropDependencies {
        override val eventListener: EventListener by Provider {
            rootModule.eventListener.value
        }
        override val plugin: AspeKt by Provider {
            rootModule.plugin.value
        }
        override val configuration: PluginConfiguration by Provider {
            rootModule.pluginConfig.value
        }
        override val cropDupeController: CropDupeController by Single {
            CropDupeController(
                pluginConfigDep = rootModule.pluginConfig
            )
        }
    }
}