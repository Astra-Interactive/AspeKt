package ru.astrainteractive.aspekt.event.restrictions.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface RestrictionsDependencies {
    val eventListener: EventListener
    val plugin: AspeKt
    val configuration: PluginConfiguration

    class Default(coreModule: CoreModule) : RestrictionsDependencies {
        override val eventListener: EventListener by Provider {
            coreModule.eventListener.value
        }
        override val plugin: AspeKt by Provider {
            coreModule.plugin.value
        }
        override val configuration: PluginConfiguration by Provider {
            coreModule.pluginConfig.value
        }
    }
}
