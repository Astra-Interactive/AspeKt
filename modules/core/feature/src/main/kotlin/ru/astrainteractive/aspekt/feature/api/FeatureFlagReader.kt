package ru.astrainteractive.aspekt.feature.api

import ru.astrainteractive.aspekt.feature.model.FeatureFlag

fun interface FeatureFlagReader {
    fun read(): FeatureFlag
}
