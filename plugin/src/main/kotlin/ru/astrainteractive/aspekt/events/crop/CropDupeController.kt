package ru.astrainteractive.aspekt.events.crop

import org.bukkit.Location
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue

class CropDupeController(
    pluginConfigDep: Dependency<PluginConfiguration>
) {
    private val pluginConfiguration by pluginConfigDep
    private val locationSet = HashMap<Location, Long>()
    private var lastClear = System.currentTimeMillis()
    fun isDupingAtLocation(location: Location): Boolean {
        val dupeConfig = pluginConfiguration.autoCrop.dupeProtection
        val lastTimeClicked = locationSet[location].also {
            locationSet[location] = System.currentTimeMillis()
        } ?: 0
        if (System.currentTimeMillis() - lastClear > dupeConfig.clearEveryMs) {
            locationSet.clear()
            lastClear = System.currentTimeMillis()
        }
        return System.currentTimeMillis() - lastTimeClicked < dupeConfig.locationTimeoutMs
    }
}