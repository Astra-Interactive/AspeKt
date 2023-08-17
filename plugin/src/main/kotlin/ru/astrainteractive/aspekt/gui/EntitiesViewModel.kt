package ru.astrainteractive.aspekt.gui

import kotlinx.coroutines.flow.MutableStateFlow
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import ru.astrainteractive.aspekt.gui.store.EntitiesState
import ru.astrainteractive.aspekt.gui.store.EntityData
import ru.astrainteractive.aspekt.gui.store.SortType
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.klibs.mikro.core.util.next

class EntitiesViewModel : AsyncComponent() {
    val state = MutableStateFlow<EntitiesState>(EntitiesState.Loading)
    fun loadData() {
        val world = Bukkit.getWorlds().first()
        val grouped = world.entities
            .groupBy { it.type }
            .map { EntityData(it.key, it.value.size) }
            .sortedByDescending { it.count }
        this.state.value = EntitiesState.AllEntities(list = grouped, sort = SortType.COUNT_DESC, world = world)
    }

    fun onSortClicked() {
        val state = state.value as? EntitiesState.AllEntities ?: return
        val sort = state.sort.next(SortType.values())
        val list = when (sort) {
            SortType.COUNT_ASC -> state.list.sortedBy { it.count }
            SortType.COUNT_DESC -> state.list.sortedByDescending { it.count }
            SortType.TYPE_ASC -> state.list.sortedBy { it.entityType }
            SortType.TYPE_DESC -> state.list.sortedByDescending { it.entityType }
        }
        this.state.value = state.copy(sort = sort, list = list)
    }

    fun onWorldChangeClicked() {
        val state = state.value as? EntitiesState.AllEntities ?: return
        val currentWorld = state.world
        val worlds = Bukkit.getServer().worlds
        val i = worlds.indexOf(currentWorld) + 1
        val world = if (i >= worlds.size) worlds[0] else worlds[i]

        val grouped = world.entities.groupBy { it.type }.map { EntityData(it.key, it.value.size) }
        this.state.value = state.copy(world = world, list = grouped)
    }

    fun onEntityClicked(type: EntityType) {
        val state = state.value as? EntitiesState.AllEntities ?: return
        val entities = state.world.entities.filter { it.type == type }
        this.state.value = EntitiesState.ExactEntity(entities)
    }
}
