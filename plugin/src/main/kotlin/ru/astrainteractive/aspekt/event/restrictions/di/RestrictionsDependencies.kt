package ru.astrainteractive.aspekt.event.restrictions.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface RestrictionsDependencies {
    val eventListener: EventListener
    val plugin: AspeKt
    val configuration: PluginConfiguration

    class Default(rootModule: RootModule) : RestrictionsDependencies {
        override val eventListener: EventListener by Provider {
            rootModule.eventListener.value
        }
        override val plugin: AspeKt by Provider {
            rootModule.plugin.value
        }
        override val configuration: PluginConfiguration by Provider {
            rootModule.pluginConfig.value
        }
    }
}
