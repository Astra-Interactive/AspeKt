package ru.astrainteractive.aspekt.module.autobroadcast.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface AutoBroadcastDependencies {
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val announcements: PluginConfiguration.Announcements
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(private val coreModule: CoreModule) : AutoBroadcastDependencies {
        override val scope: CoroutineScope = coreModule.scope
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
        override val announcements: PluginConfiguration.Announcements
            get() = coreModule.pluginConfig.cachedValue.announcements
    }
}
