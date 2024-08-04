package ru.astrainteractive.aspekt.event.restrictions.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface RestrictionsDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val configuration: PluginConfiguration

    class Default(coreModule: CoreModule) : RestrictionsDependencies {
        override val eventListener: EventListener = coreModule.eventListener
        override val plugin: JavaPlugin by Provider {
            coreModule.plugin.value
        }
        override val configuration: PluginConfiguration by Provider {
            coreModule.pluginConfig.value
        }
    }
}
