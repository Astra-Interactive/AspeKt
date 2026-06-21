package ru.astrainteractive.aspekt.module.oregeneration.populator

import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import ru.astrainteractive.aspekt.module.oregeneration.mapping.OreHostMaterialMapper
import ru.astrainteractive.aspekt.module.oregeneration.model.OreGenerationConfiguration
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.util.Random


internal class OreGenerationBlockPopulator(
    configKrate: CachedKrate<OreGenerationConfiguration>,
    private val oreHostMaterialMapper: OreHostMaterialMapper
) : BlockPopulator() {
    private val configuration by configKrate

    private fun shouldRemove(
        materialName: String,
        ores: Map<String, Double>,
        roll: Double
    ): Boolean {
        val chance = ores[materialName] ?: return false
        return roll < chance
    }

    private fun removeOreIfNeeded(
        region: LimitedRegion,
        ores: Map<String, Double>,
        random: Random,
        x: Int,
        y: Int,
        z: Int
    ) {
        val material = region.getType(x, y, z)
        if (!ores.containsKey(material.name)) return
        if (!shouldRemove(material.name, ores, random.nextDouble())) return
        region.setType(x, y, z, oreHostMaterialMapper.map(material))
    }

    override fun populate(
        worldInfo: WorldInfo,
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        limitedRegion: LimitedRegion
    ) {
        val currentConfiguration = configuration
        if (!currentConfiguration.enabled) return
        val ores = currentConfiguration.ores
        if (ores.isEmpty()) return

        val minX = chunkX * CHUNK_SIZE
        val minZ = chunkZ * CHUNK_SIZE
        for (x in minX until minX + CHUNK_SIZE) {
            for (z in minZ until minZ + CHUNK_SIZE) {
                for (y in worldInfo.minHeight until worldInfo.maxHeight) {
                    removeOreIfNeeded(limitedRegion, ores, random, x, y, z)
                }
            }
        }
    }

    companion object {
        private const val CHUNK_SIZE = 16
    }
}
