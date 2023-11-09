package ru.astrainteractive.aspekt.event.tc.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface TCDependencies {
    val configuration: PluginConfiguration
    val eventListener: EventListener
    val plugin: AspeKt
    val scope: CoroutineScope
    val dispatchers: BukkitDispatchers

    class Default(
        rootModule: RootModule
    ) : TCDependencies {
        override val configuration: PluginConfiguration by Provider {
            rootModule.pluginConfig.value
        }
        override val eventListener: EventListener by Provider {
            rootModule.eventListener.value
        }
        override val plugin: AspeKt by Provider {
            rootModule.plugin.value
        }
        override val scope: CoroutineScope by Provider {
            rootModule.scope.value
        }
        override val dispatchers: BukkitDispatchers by Provider {
            rootModule.dispatchers.value
        }
    }
}
