package ru.astrainteractive.aspekt.feature

import ru.astrainteractive.aspekt.feature.gate.FeatureGate
import ru.astrainteractive.aspekt.feature.gate.mapEnabled
import ru.astrainteractive.aspekt.feature.gate.withFeature
import ru.astrainteractive.aspekt.feature.model.ModuleState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame

class FeatureGateExtTest {

    private fun createParentGate(
        flagReader: FakeFeatureFlagReader,
        events: MutableList<String>
    ): FeatureGate<FakeFeature> = FeatureGate(
        featureClass = FakeFeature::class,
        flagReader = flagReader,
        featureFactory = { FakeFeature("parent", events) },
        lifecycleSelector = FakeFeature::lifecycle
    )

    private fun createChildGate(
        parentGate: FeatureGate<FakeFeature>,
        flagReader: FakeFeatureFlagReader,
        events: MutableList<String>,
        receivedParents: MutableList<FakeFeature> = mutableListOf()
    ): FeatureGate<FakeFeature> = parentGate.mapEnabled(
        featureClass = FakeFeature::class,
        flagReader = flagReader,
        lifecycleSelector = FakeFeature::lifecycle,
        transform = { parentFeature ->
            receivedParents.add(parentFeature)
            FakeFeature("child", events)
        }
    )

    private fun requireEnabledFeature(gate: FeatureGate<FakeFeature>): FakeFeature {
        val state = gate.currentState
        assertIs<ModuleState.Enabled<FakeFeature>>(state)
        return state.feature
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
        val childGate = createChildGate(parentGate, FakeFeatureFlagReader(isEnabled = false), events)

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
        val childGate = createChildGate(parentGate, FakeFeatureFlagReader(isEnabled = true), events)
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
        val childGate = createChildGate(parentGate, FakeFeatureFlagReader(isEnabled = true), events)
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
        val childGate = createChildGate(parentGate, childFlagReader, events)
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
        val childGate = createChildGate(parentGate, FakeFeatureFlagReader(isEnabled = true), events)
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
        val childGate = createChildGate(rootGate, FakeFeatureFlagReader(isEnabled = true), events)
        val grandChildGate = childGate.mapEnabled(
            featureClass = FakeFeature::class,
            flagReader = FakeFeatureFlagReader(isEnabled = true),
            lifecycleSelector = FakeFeature::lifecycle,
            transform = { _ -> FakeFeature("grandchild", events) }
        )
        val gatesInEnableOrder = listOf(rootGate, childGate, grandChildGate)

        gatesInEnableOrder.forEach(FeatureGate<*>::onEnable)
        assertEquals(listOf("parent.enable", "child.enable", "grandchild.enable"), events)
        events.clear()

        rootFlagReader.isEnabled = false
        gatesInEnableOrder.forEach(FeatureGate<*>::onReload)

        assertEquals(listOf("parent.disable", "child.disable", "grandchild.disable"), events)
    }

    @Test
    fun GIVEN_disabled_gate_WHEN_withFeature_THEN_returns_null() {
        val parentGate = createParentGate(FakeFeatureFlagReader(isEnabled = false), mutableListOf())

        parentGate.onEnable()

        assertNull(parentGate.withFeature { feature -> feature.enableCount })
    }

    @Test
    fun GIVEN_enabled_gate_WHEN_withFeature_THEN_returns_block_result() {
        val parentGate = createParentGate(FakeFeatureFlagReader(isEnabled = true), mutableListOf())

        parentGate.onEnable()

        assertEquals(1, parentGate.withFeature { feature -> feature.enableCount })
    }
}
