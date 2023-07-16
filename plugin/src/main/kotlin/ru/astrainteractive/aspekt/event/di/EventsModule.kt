package ru.astrainteractive.aspekt.event.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.event.crop.CropDupeController
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.event.sort.SortController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.klibs.kdi.Module

interface EventsModule : Module {
    val plugin: AspeKt
    val configuration: PluginConfiguration
    val dispatchers: BukkitDispatchers
    val eventListener: EventListener
    val translation: PluginTranslation
    val sitController: SitController
    val cropDupeController: CropDupeController
    val sortController: SortController
    val adminPrivateController: AdminPrivateController
}
