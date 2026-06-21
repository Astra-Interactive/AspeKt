package ru.astrainteractive.aspekt.module.oregeneration.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("MagicNumber")
@Serializable
internal data class OreGenerationConfiguration(
    @SerialName("enabled")
    val enabled: Boolean = true,
    @SerialName("ores")
    val ores: Map<String, Double> = listOf(
        "COAL_ORE",
        "DEEPSLATE_COAL_ORE",
        "IRON_ORE",
        "DEEPSLATE_IRON_ORE",
        "COPPER_ORE",
        "DEEPSLATE_COPPER_ORE",
        "GOLD_ORE",
        "DEEPSLATE_GOLD_ORE",
        "REDSTONE_ORE",
        "DEEPSLATE_REDSTONE_ORE",
        "LAPIS_ORE",
        "DEEPSLATE_LAPIS_ORE",
        "DIAMOND_ORE",
        "DEEPSLATE_DIAMOND_ORE",
        "EMERALD_ORE",
        "DEEPSLATE_EMERALD_ORE",
        "NETHER_GOLD_ORE",
        "NETHER_QUARTZ_ORE",
        "ANCIENT_DEBRIS"
    ).associateWith { 0.5 }
)
