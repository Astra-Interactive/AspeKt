package ru.astrainteractive.aspekt.event.sit.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.getValue

interface SitDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val configuration: PluginConfiguration
    val sitController: SitController

    class Default(
        coreModule: CoreModule,
        sitModule: SitModule,
    ) : SitDependencies {
        override val sitController: SitController = sitModule.sitController

        override val eventListener: EventListener = coreModule.eventListener
        override val plugin: JavaPlugin by coreModule.plugin

        override val configuration: PluginConfiguration by coreModule.pluginConfig
    }
}
