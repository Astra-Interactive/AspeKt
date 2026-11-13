package ru.astrainteractive.aspekt.feature.di

import ru.astrainteractive.aspekt.feature.fake.FakeFeature
import ru.astrainteractive.aspekt.feature.fake.FakeFeatureFlagReader
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
    ): FeatureGate<FakeFeature> {
        return FeatureGate(
            featureClass = FakeFeature::class,
            flagReader = flagReader,
            featureFactory = featureFactory,
            lifecycleSelector = FakeFeature::lifecycle
        )
    }

    private fun requireEnabledFeature(gate: FeatureGate<FakeFeature>): FakeFeature {
        val state = gate.currentState
        assertIs<ModuleState.Enabled<FakeFeature>>(state)
        return state.feature
    }

    private fun createParentGate(
        flagReader: FakeFeatureFlagReader,
        events: MutableList<String>
    ): FeatureGate<FakeFeature> {
        return createGate(
            flagReader = flagReader,
            featureFactory = { FakeFeature("parent", events) }
        )
    }

    private fun createChildGate(
        parentGate: FeatureGate<FakeFeature>,
        flagReader: FakeFeatureFlagReader,
        events: MutableList<String>,
        receivedParents: MutableList<FakeFeature> = mutableListOf()
    ): FeatureGate<FakeFeature> {
        return parentGate.deriveEnabled(
            featureClass = FakeFeature::class,
            flagReader = flagReader,
            lifecycleSelector = FakeFeature::lifecycle,
            transform = { parentFeature ->
                receivedParents.add(parentFeature)
                FakeFeature("child", events)
            }
        )
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

    @Test
    fun GIVEN_both_flags_on_WHEN_both_enabled_in_order_THEN_child_built_from_parent_feature() {
        val events = mutableListOf<String>()
        val receivedParents = mutableListOf<FakeFeature>()
        val parentGate = createParentGate(FakeFeatureFlagReader(isEnabled = true), events)
        val childGate = createChildGate(
            parentGate = parentGate,
            flagReader = FakeFeatureFlagReader(isEnabled = true),
            events = events,
            receivedParents = receivedParents
        )

        parentGate.onEnable()
        childGate.onEnable()

        assertEquals(listOf("parent.enable", "child.enable"), events)
        assertSame(requireEnabledFeature(parentGate), receivedParents.single())
    }

    @Test
    fun GIVEN_child_flag_off_WHEN_both_enabled_THEN_child_stays_disabled() {
        val events = mutableListOf<String>()
        val parentGate = createParentGate(FakeFeatureFlagReader(isEnabled = true), events)
        val childGate = createChildGate(
            parentGate,
            FakeFeatureFlagReader(isEnabled = false),
            events
        )

        parentGate.onEnable()
        childGate.onEnable()

        assertEquals(ModuleState.Disabled, childGate.currentState)
    }

    @Test
    fun GIVEN_parent_flag_off_WHEN_child_enable_THEN_child_stays_disabled_and_not_built() {
        val events = mutableListOf<String>()
        val receivedParents = mutableListOf<FakeFeature>()
        val parentGate = createParentGate(FakeFeatureFlagReader(isEnabled = false), events)
        val childGate = createChildGate(
            parentGate = parentGate,
            flagReader = FakeFeatureFlagReader(isEnabled = true),
            events = events,
            receivedParents = receivedParents
        )

        parentGate.onEnable()
        childGate.onEnable()

        assertEquals(ModuleState.Disabled, childGate.currentState)
        assertEquals(emptyList(), receivedParents)
    }

    @Test
    fun GIVEN_both_enabled_WHEN_parent_flag_off_and_both_reloaded_THEN_child_disabled_too() {
        val events = mutableListOf<String>()
        val parentFlagReader = FakeFeatureFlagReader(isEnabled = true)
        val parentGate = createParentGate(parentFlagReader, events)
        val childGate = createChildGate(
            parentGate,
            FakeFeatureFlagReader(isEnabled = true),
            events
        )
        parentGate.onEnable()
        childGate.onEnable()
        events.clear()

        parentFlagReader.isEnabled = false
        parentGate.onReload()
        childGate.onReload()

        assertEquals(listOf("parent.disable", "child.disable"), events)
        assertEquals(ModuleState.Disabled, childGate.currentState)
    }

    @Test
    fun GIVEN_child_enabled_WHEN_parent_went_down_first_THEN_child_disables_on_next_sync() {
        val events = mutableListOf<String>()
        val parentFlagReader = FakeFeatureFlagReader(isEnabled = true)
        val parentGate = createParentGate(parentFlagReader, events)
        val childGate = createChildGate(
            parentGate,
            FakeFeatureFlagReader(isEnabled = true),
            events
        )
        parentGate.onEnable()
        childGate.onEnable()

        parentFlagReader.isEnabled = false
        parentGate.onReload()
        assertIs<ModuleState.Enabled<FakeFeature>>(childGate.currentState)
        childGate.onReload()

        assertEquals(ModuleState.Disabled, childGate.currentState)
    }

    @Test
    fun GIVEN_parent_restarted_WHEN_both_reloaded_twice_THEN_child_rebuilt_with_new_parent_instance() {
        val events = mutableListOf<String>()
        val receivedParents = mutableListOf<FakeFeature>()
        val parentFlagReader = FakeFeatureFlagReader(isEnabled = true)
        val parentGate = createParentGate(parentFlagReader, events)
        val childGate = createChildGate(
            parentGate = parentGate,
            flagReader = FakeFeatureFlagReader(isEnabled = true),
            events = events,
            receivedParents = receivedParents
        )
        parentGate.onEnable()
        childGate.onEnable()
        val firstChildFeature = requireEnabledFeature(childGate)

        parentFlagReader.isEnabled = false
        parentGate.onReload()
        childGate.onReload()
        parentFlagReader.isEnabled = true
        parentGate.onReload()
        childGate.onReload()

        val secondChildFeature = requireEnabledFeature(childGate)
        assertNotSame(firstChildFeature, secondChildFeature)
        assertEquals(2, receivedParents.size)
        assertNotSame(receivedParents[0], receivedParents[1])
        assertSame(requireEnabledFeature(parentGate), receivedParents[1])
    }

    @Test
    fun GIVEN_both_enabled_WHEN_child_flag_off_and_child_reload_THEN_parent_stays_enabled() {
        val events = mutableListOf<String>()
        val childFlagReader = FakeFeatureFlagReader(isEnabled = true)
        val parentGate = createParentGate(FakeFeatureFlagReader(isEnabled = true), events)
        val childGate = createChildGate(
            parentGate,
            childFlagReader,
            events
        )
        parentGate.onEnable()
        childGate.onEnable()

        childFlagReader.isEnabled = false
        childGate.onReload()

        assertEquals(ModuleState.Disabled, childGate.currentState)
        assertIs<ModuleState.Enabled<FakeFeature>>(parentGate.currentState)
        assertEquals(0, requireEnabledFeature(parentGate).disableCount)
    }

    @Test
    fun GIVEN_child_blocked_by_parent_WHEN_parent_enabled_and_child_reload_THEN_child_enables() {
        val events = mutableListOf<String>()
        val parentFlagReader = FakeFeatureFlagReader(isEnabled = false)
        val parentGate = createParentGate(parentFlagReader, events)
        val childGate = createChildGate(
            parentGate,
            FakeFeatureFlagReader(isEnabled = true),
            events
        )
        parentGate.onEnable()
        childGate.onEnable()
        assertEquals(ModuleState.Disabled, childGate.currentState)

        parentFlagReader.isEnabled = true
        parentGate.onReload()
        childGate.onReload()

        assertEquals(1, requireEnabledFeature(childGate).enableCount)
    }

    @Test
    fun GIVEN_chain_of_three_WHEN_lifecycles_called_in_order_THEN_enable_and_teardown_follow_call_order() {
        val events = mutableListOf<String>()
        val rootFlagReader = FakeFeatureFlagReader(isEnabled = true)
        val rootGate = createParentGate(rootFlagReader, events)
        val childGate = createChildGate(
            rootGate,
            FakeFeatureFlagReader(isEnabled = true),
            events
        )
        val grandChildGate = childGate.deriveEnabled(
            featureClass = FakeFeature::class,
            flagReader = FakeFeatureFlagReader(isEnabled = true),
            lifecycleSelector = FakeFeature::lifecycle,
            transform = { _ -> FakeFeature("grandchild", events) }
        )
        val gatesInEnableOrder = listOf(
            rootGate,
            childGate,
            grandChildGate
        )

        gatesInEnableOrder.forEach(FeatureGate<*>::onEnable)
        val enableEvents = listOf(
            "parent.enable",
            "child.enable",
            "grandchild.enable"
        )
        assertEquals(enableEvents, events)
        events.clear()

        rootFlagReader.isEnabled = false
        gatesInEnableOrder.forEach(FeatureGate<*>::onReload)

        val disableEvents = listOf(
            "parent.disable",
            "child.disable",
            "grandchild.disable"
        )
        assertEquals(disableEvents, events)
    }
}
