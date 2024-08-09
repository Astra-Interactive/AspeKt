package ru.astrainteractive.aspekt.module.autocrop.mapping

import org.bukkit.Material

internal interface CropSeedMaterialMapper {
    /**
     * Converts crop material to it's seed material
     */
    fun toMaterial(material: Material): Material?
}

internal class CropSeedMaterialMapperImpl : CropSeedMaterialMapper {
    override fun toMaterial(material: Material): Material? {
        return when (material) {
            Material.POTATOES -> null
            Material.CARROTS -> null
            Material.BEETROOTS -> Material.BEETROOT_SEEDS
            Material.SWEET_BERRIES -> Material.SWEET_BERRIES
            Material.WHEAT -> Material.WHEAT_SEEDS
            else -> null
        }
    }
}
