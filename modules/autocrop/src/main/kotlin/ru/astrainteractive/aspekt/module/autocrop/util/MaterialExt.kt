package ru.astrainteractive.aspekt.module.autocrop.util

import org.bukkit.Material

internal fun Material.toCropMaterial(): Material? = when (this) {
    Material.POTATOES -> Material.POTATO
    Material.CARROTS -> Material.CARROT
    Material.BEETROOTS -> Material.BEETROOT
    Material.SWEET_BERRIES -> Material.SWEET_BERRIES
    Material.WHEAT -> Material.WHEAT
    else -> null
}

internal fun Material.toCropSeedMaterial(): Material? = when (this) {
    Material.BEETROOTS -> Material.BEETROOT_SEEDS
    Material.SWEET_BERRIES -> Material.SWEET_BERRIES
    Material.WHEAT -> Material.WHEAT_SEEDS
    else -> null
}
