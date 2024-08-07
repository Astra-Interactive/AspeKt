package ru.astrainteractive.aspekt.event.tc.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface TCDependencies {
    val configuration: PluginConfiguration
    val eventListener: EventListener
    val plugin: JavaPlugin
    val scope: CoroutineScope
    val dispatchers: BukkitDispatchers

    class Default(
        coreModule: CoreModule
    ) : TCDependencies {
        override val configuration: PluginConfiguration by Provider {
            coreModule.pluginConfig.value
        }
        override val eventListener: EventListener = coreModule.eventListener
        override val plugin: JavaPlugin by coreModule.plugin
        override val scope: CoroutineScope = coreModule.scope
        override val dispatchers: BukkitDispatchers = coreModule.dispatchers
    }
}
