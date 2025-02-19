package ru.astrainteractive.aspekt.inventorysort.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.inventorysort.event.sort.SortEvent
import ru.astrainteractive.aspekt.inventorysort.event.sort.di.SortDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class InventorySortModule(coreModule: CoreModule) {
    private val sortEvent: SortEvent by lazy {
        val sortDependencies: SortDependencies = SortDependencies.Default(coreModule)
        SortEvent(sortDependencies)
    }
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            sortEvent.onEnable(coreModule.plugin)
        },
        onDisable = {
            sortEvent.onDisable()
        }
    )
}