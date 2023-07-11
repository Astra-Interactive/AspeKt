package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.di.ControllersModule
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.events.di.EventsModule
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.getValue

object EventsModuleImpl : EventsModule {
    private val rootModule: RootModule by RootModuleImpl
    private val controllersModule: ControllersModule by ControllersModuleImpl

    override val plugin: Dependency<AspeKt> = rootModule.plugin
    override val configuration: Dependency<PluginConfiguration> = rootModule.pluginConfig
    override val dispatchers: Dependency<BukkitDispatchers> = rootModule.dispatchers
    override val eventListener: Dependency<EventListener> = rootModule.eventListener
    override val translation: Dependency<PluginTranslation> = rootModule.translation
    override val sitController: Single<SitController> = controllersModule.sitController
}
