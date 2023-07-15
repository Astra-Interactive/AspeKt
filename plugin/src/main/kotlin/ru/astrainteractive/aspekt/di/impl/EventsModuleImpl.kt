package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.di.ControllersModule
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.crop.CropDupeController
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.event.sort.SortController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class EventsModuleImpl(
    rootModule: RootModule
) : EventsModule {
    private val controllersModule: ControllersModule by rootModule.controllersModule

    override val plugin: AspeKt by rootModule.plugin
    override val configuration: PluginConfiguration by rootModule.pluginConfig
    override val dispatchers: BukkitDispatchers by rootModule.dispatchers
    override val eventListener: EventListener by rootModule.eventListener
    override val translation: PluginTranslation by rootModule.translation
    override val sitController: SitController by Provider { controllersModule.sitController }
    override val cropDupeController: CropDupeController by Single {
        CropDupeController(rootModule.pluginConfig)
    }
    override val sortController: SortController by Provider {
        rootModule.controllersModule.sortControllers
    }
    override val adminPrivateController: AdminPrivateController by Provider {
        rootModule.controllersModule.adminPrivateController
    }
}
