package ru.astrainteractive.aspekt.module.oregeneration.model

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("MagicNumber")
@Serializable
internal data class LootGenerationConfiguration(
    @SerialName("enabled")
    val enabled: Boolean = true,
    @SerialName("items")
    @YamlComment("The chance in range [0.0, 1.0] that each unit of the loot item will be excluded")
    val items: Map<String, Double> = listOf(
        "COAL",
        "RAW_IRON",
        "IRON_INGOT",
        "IRON_NUGGET",
        "RAW_COPPER",
        "COPPER_INGOT",
        "RAW_GOLD",
        "GOLD_INGOT",
        "GOLD_NUGGET",
        "REDSTONE",
        "LAPIS_LAZULI",
        "QUARTZ",
        "DIAMOND",
        "EMERALD",
        "NETHERITE_SCRAP",
        "NETHERITE_INGOT",
        "ANCIENT_DEBRIS"
    ).associateWith { 0.5 }
)
