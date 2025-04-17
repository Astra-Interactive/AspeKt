package ru.astrainteractive.aspekt.core.forge.model

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.storage.ServerLevelData
import kotlin.math.pow
import kotlin.math.sqrt

data class Location(
    val x: Double,
    val y: Double,
    val z: Double,
    val worldName: String
)

fun Location.round(): Location {
    return copy(
        x = x.toInt().toDouble(),
        y = y.toInt().toDouble(),
        z = z.toInt().toDouble(),
    )
}

fun Location.dist(other: Location): Double {
    return sqrt((x - other.x).pow(2.0) + (y - other.y).pow(2.0) + (z - other.z).pow(2.0))
}

fun Entity.getLocation(): Location {
    return Location(
        x = this.x,
        y = this.y,
        z = this.z,
        worldName = (level().levelData as ServerLevelData).levelName
    )
}
