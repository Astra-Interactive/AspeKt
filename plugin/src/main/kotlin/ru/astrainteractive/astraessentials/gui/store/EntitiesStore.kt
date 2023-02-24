package ru.astrainteractive.astraessentials.gui.store

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

class EntityData(
    val entityType: EntityType,
    val count: Int
)

enum class SortType {
    COUNT_ASC, COUNT_DESC, TYPE_ASC, TYPE_DESC
}

sealed interface EntitiesState {
    object Loading : EntitiesState
    data class AllEntities(
        val list: List<EntityData>,
        val sort: SortType,
        val world: World
    ) : EntitiesState

    data class ExactEntity(
        val list: List<Entity>,
    ) : EntitiesState
}
