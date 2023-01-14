package ru.astrainteractive.astraessentials.utils

import org.bukkit.configuration.file.FileConfiguration
import ru.astrainteractive.astralibs.utils.getFloat
import ru.astrainteractive.astralibs.configuration.configuration
import ru.astrainteractive.astralibs.utils.HEX

fun FileConfiguration.cString(path: String, default: String) = configuration(path) {
    if (!this.contains(path)) this.set(path, default)
    this.getString(path, default) ?: default
}

fun FileConfiguration.cStringList(path: String) = configuration(path) {
    if (!this.contains(path)) this.set(path, emptyList<String>())
    this.getStringList(path).map { it.HEX() }
}

fun FileConfiguration.cBoolean(path: String, default: Boolean) = configuration(path) {
    if (!this.contains(path)) this.set(path, default)
    this.getBoolean(path, default)
}

fun FileConfiguration.cDouble(path: String, default: Double) = configuration(path) {
    if (!this.contains(path)) this.set(path, default)
    this.getDouble(path, default)
}
fun FileConfiguration.cInt(path: String, default: Int) = configuration(path) {
    if (!this.contains(path)) this.set(path, default)
    this.getInt(path, default)
}

fun FileConfiguration.cFloat(path: String, default: Float) = configuration(path) {
    if (!this.contains(path)) this.set(path, default)
    this.getFloat(path, default)
}