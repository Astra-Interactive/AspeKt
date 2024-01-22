package ru.astrainteractive.aspekt.event.crop.mapping

import org.bukkit.Material

interface CropMaterialMapper {
    /**
     * Converts crop material to it's material
     */
    fun toMaterial(material: Material): Material?
}
class CropMaterialMapperImpl : CropMaterialMapper {
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
