@file:Suppress("KindImportDirection")

package ru.astrainteractive.aspekt.feature.util

import ru.astrainteractive.aspekt.feature.api.FeatureFlagReader
import ru.astrainteractive.aspekt.feature.di.FeatureGate
import ru.astrainteractive.aspekt.feature.di.ParentGatedFeatureFlagReader
import ru.astrainteractive.aspekt.feature.model.ModuleState
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import kotlin.reflect.KClass

fun <T : Any, R> FeatureGate<T>.withFeature(block: (T) -> R): R? {
    return when (val current = currentState) {
        is ModuleState.Enabled -> block.invoke(current.feature)
        ModuleState.Disabled -> null
    }
}

fun <T : Any, R : Any> FeatureGate<T>.deriveEnabled(
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
