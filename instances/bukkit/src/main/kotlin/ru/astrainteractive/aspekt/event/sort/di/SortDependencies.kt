package ru.astrainteractive.aspekt.event.sort.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.sort.SortController
import ru.astrainteractive.astralibs.event.EventListener

interface SortDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val sortController: SortController

    class Default(
        coreModule: CoreModule
    ) : SortDependencies {
        override val eventListener: EventListener = coreModule.eventListener
        override val plugin: JavaPlugin = coreModule.plugin
        override val sortController: SortController by lazy {
            SortController()
        }
    }
}
