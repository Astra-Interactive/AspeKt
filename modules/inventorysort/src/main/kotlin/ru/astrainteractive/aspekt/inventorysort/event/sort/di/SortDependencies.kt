package ru.astrainteractive.aspekt.inventorysort.event.sort.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.inventorysort.event.sort.SortController
import ru.astrainteractive.astralibs.event.EventListener

internal interface SortDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val sortController: SortController

    class Default(
        bukkitCoreModule: BukkitCoreModule
    ) : SortDependencies {
        override val eventListener: EventListener = bukkitCoreModule.eventListener
        override val plugin: JavaPlugin = bukkitCoreModule.plugin
        override val sortController: SortController by lazy {
            SortController()
        }
    }
}
