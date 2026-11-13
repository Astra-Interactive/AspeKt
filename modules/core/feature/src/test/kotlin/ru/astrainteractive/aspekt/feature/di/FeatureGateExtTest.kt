@file:Suppress("TestMirror")

package ru.astrainteractive.aspekt.feature.di

import ru.astrainteractive.aspekt.feature.fake.FakeFeature
import ru.astrainteractive.aspekt.feature.fake.FakeFeatureFlagReader
import ru.astrainteractive.aspekt.feature.util.withFeature
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FeatureGateExtTest {

    private fun createParentGate(
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

    @Test
    fun GIVEN_disabled_gate_WHEN_with_feature_THEN_returns_null() {
        val parentGate = createParentGate(FakeFeatureFlagReader(isEnabled = false), mutableListOf())

        parentGate.onEnable()

        assertNull(parentGate.withFeature(FakeFeature::enableCount))
    }

    @Test
    fun GIVEN_enabled_gate_WHEN_with_feature_THEN_returns_block_result() {
        val parentGate = createParentGate(FakeFeatureFlagReader(isEnabled = true), mutableListOf())

        parentGate.onEnable()

        assertEquals(1, parentGate.withFeature(FakeFeature::enableCount))
    }
}
