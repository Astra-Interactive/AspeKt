package ru.astrainteractive.aspekt.module.autocrop.mapping

import org.bukkit.Material

internal interface CropMaterialMapper {
    /**
     * Converts crop material to it's material
     */
    fun toMaterial(material: Material): Material?
}
internal class CropMaterialMapperImpl : CropMaterialMapper {
    override fun toMaterial(material: Material): Material? {
        return when (material) {
            Material.POTATOES -> Material.POTATO
            Material.CARROTS -> Material.CARROT
            Material.BEETROOTS -> Material.BEETROOT
            Material.SWEET_BERRIES -> Material.SWEET_BERRIES
            Material.WHEAT -> Material.WHEAT
            else -> null
        }
    }
}
