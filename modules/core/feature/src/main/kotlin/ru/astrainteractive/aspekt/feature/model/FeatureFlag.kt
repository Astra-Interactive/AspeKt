package ru.astrainteractive.aspekt.feature.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlag(
    @SerialName("is_enabled")
    val isEnabled: Boolean = false
) {
    companion object {
        val DISABLED: FeatureFlag = FeatureFlag(false)
    }
}
