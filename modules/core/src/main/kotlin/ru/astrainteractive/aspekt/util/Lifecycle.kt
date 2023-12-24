package ru.astrainteractive.aspekt.util

/**
 * Lifecycle integration for object component
 */
interface Lifecycle {

    fun onEnable() = Unit

    fun onDisable() = Unit

    fun onReload() = Unit

    class Lambda(
        private val onEnable: () -> Unit = {},
        private val onDisable: () -> Unit = {},
        private val onReload: () -> Unit = {},
    ) : Lifecycle {
        override fun onEnable() {
            onEnable.invoke()
        }

        override fun onDisable() {
            onDisable.invoke()
        }

        override fun onReload() {
            onReload.invoke()
        }
    }
}
