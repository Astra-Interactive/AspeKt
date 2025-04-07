package ru.astrainteractive.aspekt.inventorysort.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.inventorysort.event.sort.SortEvent
import ru.astrainteractive.aspekt.inventorysort.event.sort.di.SortDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class InventorySortModule(
    bukkitCoreModule: BukkitCoreModule
) {
    private val sortEvent: SortEvent by lazy {
        val sortDependencies = SortDependencies.Default(bukkitCoreModule)
        SortEvent(sortDependencies)
    }
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            sortEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            sortEvent.onDisable()
        }
    )
}
