package ru.astrainteractive.aspekt.module.sit.event.sit.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.sit.event.sit.SitController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.event.EventListener

internal interface SitDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val configuration: PluginConfiguration
    val sitController: SitController

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        override val sitController: SitController
    ) : SitDependencies {

        override val eventListener: EventListener = bukkitCoreModule.eventListener
        override val plugin: JavaPlugin = bukkitCoreModule.plugin

        override val configuration: PluginConfiguration by coreModule.pluginConfig
    }
}
