package ru.astrainteractive.aspekt.module.oregeneration.mapping

import org.bukkit.Material

internal class OreHostMaterialMapper {
    fun map(ore: Material): Material = when {
        ore.name.startsWith("DEEPSLATE", ignoreCase = true) -> Material.DEEPSLATE
        ore == Material.NETHER_GOLD_ORE -> Material.NETHERRACK
        ore == Material.NETHER_QUARTZ_ORE -> Material.NETHERRACK
        ore == Material.ANCIENT_DEBRIS -> Material.NETHERRACK
        else -> Material.STONE
    }
}
