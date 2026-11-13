package ru.astrainteractive.aspekt.feature.di

import ru.astrainteractive.aspekt.feature.fake.FakeFeature
import ru.astrainteractive.aspekt.feature.fake.FakeFeatureFlagReader

internal fun createParentGate(
    flagReader: FakeFeatureFlagReader,
    events: MutableList<String>
): FeatureGate<FakeFeature> {
    return FeatureGate(
        featureClass = FakeFeature::class,
        flagReader = flagReader,
        featureFactory = { FakeFeature("parent", events) },
        lifecycleSelector = FakeFeature::lifecycle
    )
}
