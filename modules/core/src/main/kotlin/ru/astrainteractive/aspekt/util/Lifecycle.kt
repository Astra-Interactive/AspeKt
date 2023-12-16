package ru.astrainteractive.aspekt.util

/**
 * Lifecycle integration for object component
 */
interface Lifecycle {

    fun onEnable() = Unit

    fun onDisable() = Unit

    fun onReload() = Unit
}
