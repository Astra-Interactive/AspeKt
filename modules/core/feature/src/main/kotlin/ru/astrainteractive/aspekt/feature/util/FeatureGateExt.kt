@file:Suppress("KindImportDirection")

package ru.astrainteractive.aspekt.feature.util

import ru.astrainteractive.aspekt.feature.di.FeatureGate
import ru.astrainteractive.aspekt.feature.model.ModuleState

fun <T : Any, R> FeatureGate<T>.withFeature(block: (T) -> R): R? {
    return when (val current = currentState) {
        is ModuleState.Enabled -> block.invoke(current.feature)
        ModuleState.Disabled -> null
    }
}
