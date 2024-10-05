package ru.astrainteractive.aspekt.event.restrictions.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.event.EventListener

interface RestrictionsDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val configuration: PluginConfiguration

    class Default(coreModule: CoreModule) : RestrictionsDependencies {
        override val eventListener: EventListener = coreModule.eventListener
        override val plugin: JavaPlugin = coreModule.plugin
        override val configuration: PluginConfiguration by coreModule.pluginConfig
    }
}
