package ru.astrainteractive.aspekt.core.forge.model

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.storage.ServerLevelData
import kotlin.math.pow
import kotlin.math.sqrt
import ru.astrainteractive.aspekt.minecraft.location.Location

fun Entity.getLocation(): Location {
    return Location(
        x = this.x,
        y = this.y,
        z = this.z,
        worldName = (level().levelData as ServerLevelData).levelName
    )
}
