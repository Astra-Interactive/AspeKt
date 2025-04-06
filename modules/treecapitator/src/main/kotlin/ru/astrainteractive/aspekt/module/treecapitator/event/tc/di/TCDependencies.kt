package ru.astrainteractive.aspekt.module.treecapitator.event.tc.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface TCDependencies {
    val configuration: PluginConfiguration
    val eventListener: EventListener
    val plugin: JavaPlugin
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : TCDependencies {
        override val configuration: PluginConfiguration by coreModule.pluginConfig
        override val eventListener: EventListener = bukkitCoreModule.eventListener
        override val plugin: JavaPlugin = bukkitCoreModule.plugin
        override val scope: CoroutineScope = coreModule.scope
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
    }
}
