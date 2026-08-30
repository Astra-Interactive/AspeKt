package ru.astrainteractive.aspekt.feature.flagreader

import ru.astrainteractive.aspekt.feature.model.FeatureFlag

fun interface FeatureFlagReader {
    fun read(): FeatureFlag
}
