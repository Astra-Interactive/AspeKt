package ru.astrainteractive.aspekt.module.restrictions.event.restrictions.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kstorage.util.getValue

internal interface RestrictionsDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val configuration: PluginConfiguration

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : RestrictionsDependencies {
        override val eventListener: EventListener = bukkitCoreModule.eventListener
        override val plugin: JavaPlugin = bukkitCoreModule.plugin
        override val configuration: PluginConfiguration by coreModule.pluginConfig
    }
}
