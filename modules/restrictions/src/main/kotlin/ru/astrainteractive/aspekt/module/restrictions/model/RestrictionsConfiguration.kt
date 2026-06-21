package ru.astrainteractive.aspekt.module.restrictions.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RestrictionsConfiguration(
    @SerialName("explosion")
    val explosion: Explosion = Explosion(),
    @SerialName("place")
    val place: Place = Place(),
    @SerialName("spread")
    val spread: Spread = Spread()
) {
    @Serializable
    internal data class RestrictionRule(
        @SerialName("is_enabled")
        val isEnabled: Boolean = true,
        @SerialName("restricted_in_worlds")
        val restrictedInWorlds: List<String> = emptyList(),
        @SerialName("invert")
        val invert: Boolean = false
    )

    @Serializable
    data class Explosion(
        @SerialName("damage_creeper")
        val creeperDamage: RestrictionRule = RestrictionRule(),
        @SerialName("damage_other")
        val otherDamage: RestrictionRule = RestrictionRule(),
        @SerialName("destroy")
        val destroy: RestrictionRule = RestrictionRule()
    )

    @Serializable
    data class Place(
        @SerialName("tnt")
        val tnt: RestrictionRule = RestrictionRule(),
        @SerialName("lava")
        val lava: RestrictionRule = RestrictionRule(),
    )

    @Serializable
    data class Spread(
        @SerialName("lava")
        val lava: RestrictionRule = RestrictionRule(),
        @SerialName("fire")
        val fire: RestrictionRule = RestrictionRule()
    )
}
