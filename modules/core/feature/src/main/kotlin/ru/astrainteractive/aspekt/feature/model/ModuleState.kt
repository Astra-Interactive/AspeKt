package ru.astrainteractive.aspekt.feature.model

sealed interface ModuleState<out T> {
    data object Disabled : ModuleState<Nothing>
    class Enabled<T>(val feature: T) : ModuleState<T>
}
