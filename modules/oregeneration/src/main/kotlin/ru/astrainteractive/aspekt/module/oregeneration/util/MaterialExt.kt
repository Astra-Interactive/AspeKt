package ru.astrainteractive.aspekt.module.oregeneration.util

import org.bukkit.Material

internal fun Material.toOreHostMaterial(): Material = when {
    name.startsWith("DEEPSLATE", ignoreCase = true) -> Material.DEEPSLATE
    this == Material.NETHER_GOLD_ORE -> Material.NETHERRACK
    this == Material.NETHER_QUARTZ_ORE -> Material.NETHERRACK
    this == Material.ANCIENT_DEBRIS -> Material.NETHERRACK
    else -> Material.STONE
}
