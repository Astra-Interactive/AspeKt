package ru.astrainteractive.aspekt.inventorysort.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.inventorysort.event.SortController
import ru.astrainteractive.aspekt.inventorysort.event.SortEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class InventorySortModule(
    bukkitCoreModule: BukkitCoreModule
) {
    private val sortEvent: SortEvent by lazy {
        SortEvent(SortController())
    }

    @Suppress("TrivialLambdaReference")
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            sortEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            sortEvent.onDisable()
        }
    )

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("inventory_sort.yml")
    }
}
