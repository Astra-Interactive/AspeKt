package ru.astrainteractive.aspekt.feature.di

import ru.astrainteractive.aspekt.feature.api.FeatureFlagReader
import ru.astrainteractive.aspekt.feature.model.ModuleState
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import kotlin.reflect.KClass

class FeatureGate<T : Any>(
    featureClass: KClass<T>,
    private val flagReader: FeatureFlagReader,
    private val featureFactory: () -> T,
    private val lifecycleSelector: (T) -> Lifecycle,
) : Lifecycle,
    Logger by JUtiltLogger("AspeKt-FeatureGate-${featureClass.java.simpleName}") {

    internal val name: String = featureClass.java.simpleName

    private var state: ModuleState<T> = ModuleState.Disabled

    val currentState: ModuleState<T>
        get() = state

    private fun enableNow() {
        runCatching {
            val feature = featureFactory.invoke()
            lifecycleSelector.invoke(feature).onEnable()
            feature
        }.onSuccess { feature ->
            state = ModuleState.Enabled(feature)
            info { "Feature '$name' enabled" }
        }.onFailure { throwable ->
            error(throwable) { "Could not enable feature '$name'" }
        }
    }

    private fun disableNow() {
        val current = state as? ModuleState.Enabled<T> ?: return
        state = ModuleState.Disabled
        runCatching { lifecycleSelector.invoke(current.feature).onDisable() }
            .onFailure { throwable -> error(throwable) { "Error while disabling feature '$name'" } }
        info { "Feature '$name' disabled" }
    }

    private fun sync() {
        val shouldBeEnabled = flagReader.read().isEnabled
        val current = state
        when {
            shouldBeEnabled && current is ModuleState.Disabled -> enableNow()
            !shouldBeEnabled && current is ModuleState.Enabled -> disableNow()
        }
    }

    override fun onEnable() {
        sync()
    }

    override fun onReload() {
        val wasEnabled = state is ModuleState.Enabled
        sync()
        val current = state
        if (wasEnabled && current is ModuleState.Enabled) {
            lifecycleSelector.invoke(current.feature).onReload()
        }
    }

    override fun onDisable() {
        disableNow()
    }
}
