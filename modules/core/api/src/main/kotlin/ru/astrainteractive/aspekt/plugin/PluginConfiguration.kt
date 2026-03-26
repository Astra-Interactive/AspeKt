package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginConfiguration(
    @SerialName("sit")
    val sit: Boolean = true,
    @SerialName("auto_crop")
    val autoCrop: AutoCrop = AutoCrop(),
    @SerialName("tree_capitator")
    val treeCapitator: TreeCapitator = TreeCapitator(),
    @SerialName("restrictions")
    val restrictions: Restrictions = Restrictions(),
    @SerialName("money_drop")
    val moneyDrop: Map<String, MoneyDropEntry> = emptyMap(),
) {

    @Serializable
    data class MoneyDropEntry(
        val from: String,
        val chance: Double,
        val min: Double,
        val max: Double,
        val currencyId: String? = null
    )

    @Serializable
    data class Restrictions(
        @SerialName("explosion")
        val explosion: Explosion = Explosion(),
        @SerialName("place")
        val place: Place = Place(),
        @SerialName("spread")
        val spread: Spread = Spread()
    ) {
        @Serializable
        data class Explosion(
            @SerialName("damage_creeper")
            val creeperDamage: Boolean = true,
            @SerialName("damage_other")
            val otherDamage: Boolean = true,
            @SerialName("destroy")
            val destroy: Boolean = true
        )

        @Serializable
        data class Place(
            @SerialName("tnt")
            val tnt: Boolean = true,
            @SerialName("lava")
            val lava: Boolean = true,
        )

        @Serializable
        data class Spread(
            @SerialName("lava")
            val lava: Boolean = true,
            @SerialName("fire")
            val fire: Boolean = true
        )
    }

    @Serializable
    @Suppress("LongParameterList")
    data class TreeCapitator(
        @SerialName("enabled")
        val enabled: Boolean = true,
        @SerialName("destroy_limit")
        val destroyLimit: Int = 16,
        @SerialName("damage_axe")
        val damageAxe: Boolean = true,
        @SerialName("break_axe")
        val breakAxe: Boolean = true,
        @SerialName("replant")
        val replant: Boolean = true,
        @SerialName("replant_max_iterations")
        val replantMaxIterations: Int = 16,
        @SerialName("destroy_leaves")
        val destroyLeaves: Boolean = true
    )

    @Serializable
    data class AutoCrop(
        @SerialName("enabled")
        val enabled: Boolean = true,
        @SerialName("min")
        val min: Int = 0,
        @SerialName("max")
        val max: Int = 0,
    )

}
