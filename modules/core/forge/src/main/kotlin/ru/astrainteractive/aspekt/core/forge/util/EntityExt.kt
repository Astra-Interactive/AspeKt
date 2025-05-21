package ru.astrainteractive.aspekt.core.forge.util

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.minecraft.Locatable
import ru.astrainteractive.aspekt.minecraft.location.Location

fun Entity.asLocatable() = Locatable {
    Location(
        x = this.x,
        y = this.y,
        z = this.z,
        worldName = (level().levelData as ServerLevelData).levelName
    )
}
