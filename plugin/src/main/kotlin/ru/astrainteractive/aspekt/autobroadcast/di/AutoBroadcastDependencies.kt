package ru.astrainteractive.aspekt.autobroadcast.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal interface AutoBroadcastDependencies {
    val scope: CoroutineScope
    val dispatchers: BukkitDispatchers
    val configuration: PluginConfiguration.Announcements

    class Default(coreModule: CoreModule) : AutoBroadcastDependencies {
        override val scope: CoroutineScope by coreModule.scope
        override val dispatchers: BukkitDispatchers by coreModule.dispatchers
        override val configuration: PluginConfiguration.Announcements by Provider {
            coreModule.pluginConfig.value.announcements
        }
    }
}
