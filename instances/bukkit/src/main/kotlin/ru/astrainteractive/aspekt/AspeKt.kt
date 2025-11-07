package ru.astrainteractive.aspekt

import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin

class AspeKt : LifecyclePlugin() {
    private val rootModule = RootModule(this)

    override fun onEnable() {
        rootModule.lifecycle.onEnable()
    }

    override fun onDisable() {
        rootModule.lifecycle.onDisable()
    }

    override fun onReload() {
        rootModule.lifecycle.onReload()
    }
}
