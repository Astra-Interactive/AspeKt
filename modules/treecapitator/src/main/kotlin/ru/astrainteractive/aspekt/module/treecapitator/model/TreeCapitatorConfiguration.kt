package ru.astrainteractive.aspekt.module.treecapitator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("LongParameterList")
internal data class TreeCapitatorConfiguration(
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
