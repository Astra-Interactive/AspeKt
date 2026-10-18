package ru.astrainteractive.aspekt.feature.fake

import ru.astrainteractive.aspekt.feature.api.FeatureFlagReader
import ru.astrainteractive.aspekt.feature.model.FeatureFlag

class FakeFeatureFlagReader(
    var isEnabled: Boolean
) : FeatureFlagReader {
    override fun read(): FeatureFlag = FeatureFlag(isEnabled)
}
