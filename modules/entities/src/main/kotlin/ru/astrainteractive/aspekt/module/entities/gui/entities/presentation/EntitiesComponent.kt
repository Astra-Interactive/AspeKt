package ru.astrainteractive.aspekt.module.entities.gui.entities.presentation

import kotlinx.coroutines.flow.StateFlow
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import ru.astrainteractive.aspekt.module.entities.gui.entities.model.EntityData
import ru.astrainteractive.aspekt.module.entities.gui.entities.model.SortType

interface EntitiesComponent {
    val model: StateFlow<Model>

    fun loadData()

    fun onSortClicked()

    fun onWorldChangeClicked()

    fun onEntityClicked(type: EntityType)

    sealed interface Model {
        data object Loading : Model
        data class AllEntities(
            val list: List<EntityData>,
            val sort: SortType,
            val world: World
        ) : Model

        data class ExactEntity(
            val list: List<Entity>,
        ) : Model
    }
}
