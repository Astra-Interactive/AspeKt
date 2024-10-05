package ru.astrainteractive.aspekt.module.autobroadcast.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer

internal interface AutoBroadcastDependencies {
    val scope: CoroutineScope
    val dispatchers: BukkitDispatchers
    val configuration: PluginConfiguration.Announcements
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(private val coreModule: CoreModule) : AutoBroadcastDependencies {
        override val scope: CoroutineScope = coreModule.scope
        override val dispatchers: BukkitDispatchers = coreModule.dispatchers
        override val kyoriComponentSerializer: KyoriComponentSerializer
            get() = coreModule.kyoriComponentSerializer.cachedValue
        override val configuration: PluginConfiguration.Announcements
            get() = coreModule.pluginConfig.cachedValue.announcements
    }
}
