package ru.astrainteractive.aspekt.feature

import ru.astrainteractive.aspekt.feature.gate.FeatureGate
import ru.astrainteractive.aspekt.feature.model.ModuleState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class FeatureGateTest {

    private fun createGate(
        flagReader: FakeFeatureFlagReader,
        events: MutableList<String> = mutableListOf(),
        featureFactory: () -> FakeFeature = { FakeFeature("feature", events) }
    ): FeatureGate<FakeFeature> = FeatureGate(
        featureClass = FakeFeature::class,
        flagReader = flagReader,
        featureFactory = featureFactory,
        lifecycleSelector = FakeFeature::lifecycle
    )

    private fun requireEnabledFeature(gate: FeatureGate<FakeFeature>): FakeFeature {
        val state = gate.currentState
        assertIs<ModuleState.Enabled<FakeFeature>>(state)
        return state.feature
    }

    @Test
    fun GIVEN_disabled_flag_WHEN_enable_THEN_feature_is_not_created() {
        var factoryInvocations = 0
        val gate = createGate(
            flagReader = FakeFeatureFlagReader(isEnabled = false),
            featureFactory = {
                factoryInvocations++
                FakeFeature("feature", mutableListOf())
            }
        )

        gate.onEnable()

        assertEquals(0, factoryInvocations)
        assertEquals(ModuleState.Disabled, gate.currentState)
    }

    @Test
    fun GIVEN_enabled_flag_WHEN_enable_THEN_feature_created_and_enabled() {
        val gate = createGate(flagReader = FakeFeatureFlagReader(isEnabled = true))

        gate.onEnable()

        val feature = requireEnabledFeature(gate)
        assertEquals(1, feature.enableCount)
    }

    @Test
    fun GIVEN_enabled_gate_WHEN_enable_called_again_THEN_feature_is_not_recreated() {
        val gate = createGate(flagReader = FakeFeatureFlagReader(isEnabled = true))
        gate.onEnable()
        val firstFeature = requireEnabledFeature(gate)

        gate.onEnable()

        val secondFeature = requireEnabledFeature(gate)
        assertSame(firstFeature, secondFeature)
        assertEquals(1, firstFeature.enableCount)
    }

    @Test
    fun GIVEN_enabled_gate_WHEN_flag_turned_off_and_reload_THEN_feature_disabled_and_dropped() {
        val flagReader = FakeFeatureFlagReader(isEnabled = true)
        val gate = createGate(flagReader = flagReader)
        gate.onEnable()
        val feature = requireEnabledFeature(gate)

        flagReader.isEnabled = false
        gate.onReload()

        assertEquals(1, feature.disableCount)
        assertEquals(0, feature.reloadCount)
        assertEquals(ModuleState.Disabled, gate.currentState)
    }

    @Test
    fun GIVEN_disabled_gate_WHEN_flag_turned_on_and_reload_THEN_feature_enabled() {
        val flagReader = FakeFeatureFlagReader(isEnabled = false)
        val gate = createGate(flagReader = flagReader)
        gate.onEnable()

        flagReader.isEnabled = true
        gate.onReload()

        val feature = requireEnabledFeature(gate)
        assertEquals(1, feature.enableCount)
    }

    @Test
    fun GIVEN_enabled_gate_WHEN_reload_with_flag_still_on_THEN_same_feature_is_reloaded() {
        val gate = createGate(flagReader = FakeFeatureFlagReader(isEnabled = true))
        gate.onEnable()
        val feature = requireEnabledFeature(gate)

        gate.onReload()

        assertSame(feature, requireEnabledFeature(gate))
        assertEquals(1, feature.reloadCount)
        assertEquals(1, feature.enableCount)
    }

    @Test
    fun GIVEN_enabled_gate_WHEN_toggled_off_then_on_THEN_new_feature_instance_is_created() {
        val flagReader = FakeFeatureFlagReader(isEnabled = true)
        val gate = createGate(flagReader = flagReader)
        gate.onEnable()
        val firstFeature = requireEnabledFeature(gate)

        flagReader.isEnabled = false
        gate.onReload()
        flagReader.isEnabled = true
        gate.onReload()

        val secondFeature = requireEnabledFeature(gate)
        assertNotSame(firstFeature, secondFeature)
        assertEquals(1, firstFeature.disableCount)
        assertEquals(1, secondFeature.enableCount)
    }

    @Test
    fun GIVEN_throwing_factory_WHEN_enable_THEN_gate_stays_disabled_without_exception() {
        val gate = createGate(
            flagReader = FakeFeatureFlagReader(isEnabled = true),
            featureFactory = { error("Feature construction failed") }
        )

        gate.onEnable()

        assertEquals(ModuleState.Disabled, gate.currentState)
    }

    @Test
    fun GIVEN_enabled_gate_WHEN_disable_THEN_feature_disabled_even_with_flag_on() {
        val gate = createGate(flagReader = FakeFeatureFlagReader(isEnabled = true))
        gate.onEnable()
        val feature = requireEnabledFeature(gate)

        gate.onDisable()

        assertEquals(1, feature.disableCount)
        assertEquals(ModuleState.Disabled, gate.currentState)
    }
}
