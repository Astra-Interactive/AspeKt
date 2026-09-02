package ru.astrainteractive.aspekt.feature.gate

import ru.astrainteractive.aspekt.feature.flagreader.FeatureFlagReader
import ru.astrainteractive.aspekt.feature.flagreader.ParentGatedFeatureFlagReader
import ru.astrainteractive.aspekt.feature.model.ModuleState
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import kotlin.reflect.KClass

/**
 * Safe access to a gated feature: the reference must not be cached by the caller,
 * because a disable/enable cycle replaces the instance.
 */
fun <T : Any, R> FeatureGate<T>.withFeature(block: (T) -> R): R? {
    return when (val current = currentState) {
        is ModuleState.Enabled -> block.invoke(current.feature)
        ModuleState.Disabled -> null
    }
}

/**
 * Derives a gate whose feature is built from the parent's feature.
 * Effective state: parent enabled AND own [flagReader]; when the flag is on but
 * the parent is disabled, the derived gate logs a warning and stays disabled.
 *
 * The parent knows nothing about the derived gate, so lifecycle ordering is the
 * caller's responsibility: enable/reload the parent before the derived gate — a
 * flag change then propagates down the chain within one pass — and on shutdown
 * disable the derived gate before the parent.
 */
fun <T : Any, R : Any> FeatureGate<T>.mapEnabled(
    featureClass: KClass<R>,
    flagReader: FeatureFlagReader,
    lifecycleSelector: (R) -> Lifecycle,
    transform: (T) -> R,
): FeatureGate<R> {
    val parentFeatureGate = this
    return FeatureGate(
        featureClass = featureClass,
        flagReader = ParentGatedFeatureFlagReader(
            featureClass = featureClass,
            parentFeatureGate = parentFeatureGate,
            delegate = flagReader
        ),
        featureFactory = {
            val parentState = parentFeatureGate.currentState
            check(parentState is ModuleState.Enabled) { "Feature '${parentFeatureGate.name}' is not enabled" }
            transform.invoke(parentState.feature)
        },
        lifecycleSelector = lifecycleSelector
    )
}
