package ru.astrainteractive.aspekt.events.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener

interface EventsModule : Module {
    val plugin: Dependency<AspeKt>
    val configuration: Dependency<PluginConfiguration>
    val dispatchers: Dependency<BukkitDispatchers>
    val eventListener: Dependency<EventListener>
    val translation: Dependency<PluginTranslation>
    val sitController: Single<SitController>
}
