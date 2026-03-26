package ru.astrainteractive.aspekt.module.restrictions.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestrictionsConfiguration(
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