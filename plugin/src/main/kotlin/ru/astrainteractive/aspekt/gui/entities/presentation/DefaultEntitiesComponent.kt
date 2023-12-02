package ru.astrainteractive.aspekt.gui.entities.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import ru.astrainteractive.aspekt.gui.entities.model.EntityData
import ru.astrainteractive.aspekt.gui.entities.model.SortType
import ru.astrainteractive.aspekt.gui.entities.presentation.EntitiesComponent.Model
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.klibs.mikro.core.util.next

class DefaultEntitiesComponent : AsyncComponent(), EntitiesComponent {
    override val model = MutableStateFlow<Model>(Model.Loading)
    override fun loadData() {
        val world = Bukkit.getWorlds().first()
        val grouped = world.entities
            .groupBy { it.type }
            .map { EntityData(it.key, it.value.size) }
            .sortedByDescending { it.count }
        model.value = Model.AllEntities(list = grouped, sort = SortType.COUNT_DESC, world = world)
    }

    override fun onSortClicked() {
        val state = model.value as? Model.AllEntities ?: return
        val sort = state.sort.next(SortType.values())
        val list = when (sort) {
            SortType.COUNT_ASC -> state.list.sortedBy { it.count }
            SortType.COUNT_DESC -> state.list.sortedByDescending { it.count }
            SortType.TYPE_ASC -> state.list.sortedBy { it.entityType }
            SortType.TYPE_DESC -> state.list.sortedByDescending { it.entityType }
        }
        model.value = state.copy(sort = sort, list = list)
    }

    override fun onWorldChangeClicked() {
        val state = model.value as? Model.AllEntities ?: return
        val currentWorld = state.world
        val worlds = Bukkit.getServer().worlds
        val i = worlds.indexOf(currentWorld) + 1
        val world = if (i >= worlds.size) worlds[0] else worlds[i]

        val grouped = world.entities.groupBy { it.type }.map { EntityData(it.key, it.value.size) }
        model.value = state.copy(world = world, list = grouped)
    }

    override fun onEntityClicked(type: EntityType) {
        val state = model.value as? Model.AllEntities ?: return
        val entities = state.world.entities.filter { it.type == type }
        model.value = Model.ExactEntity(entities)
    }
}
