package ru.astrainteractive.aspekt.feature.flagreader

import ru.astrainteractive.aspekt.feature.gate.FeatureGate
import ru.astrainteractive.aspekt.feature.model.FeatureFlag
import ru.astrainteractive.aspekt.feature.model.ModuleState
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import kotlin.reflect.KClass

internal class ParentGatedFeatureFlagReader(
    featureClass: KClass<*>,
    private val parentFeatureGate: FeatureGate<*>,
    private val delegate: FeatureFlagReader,
) : FeatureFlagReader,
    Logger by JUtiltLogger("AspeKt-FeatureGate-${featureClass.java.simpleName}") {

    private val name: String = featureClass.java.simpleName

    override fun read(): FeatureFlag {
        val flag = delegate.read()
        if (!flag.isEnabled) return flag
        if (parentFeatureGate.currentState is ModuleState.Enabled) return flag
        warn {
            "Feature '$name' is enabled by flag, " +
                "but parent feature '${parentFeatureGate.name}' is disabled"
        }
        return FeatureFlag.DISABLED
    }
}
