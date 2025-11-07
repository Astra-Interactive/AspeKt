package ru.astrainteractive.aspekt.inventorysort.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.inventorysort.event.sort.SortController
import ru.astrainteractive.aspekt.inventorysort.event.sort.SortEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class InventorySortModule(
    bukkitCoreModule: BukkitCoreModule
) {
    private val sortEvent: SortEvent by lazy {
        SortEvent(SortController())
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
