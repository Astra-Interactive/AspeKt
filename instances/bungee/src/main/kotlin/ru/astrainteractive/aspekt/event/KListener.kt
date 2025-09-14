package ru.astrainteractive.aspekt.event

import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin

interface KListener : Listener {
    fun register(plugin: Plugin) {
        plugin.proxy.pluginManager.registerListener(plugin, this)
    }

    fun unregister(plugin: Plugin) {
        plugin.proxy.pluginManager.unregisterListener(this)
    }
}
