@file:Suppress("Filename")

package ru.astrainteractive.aspekt.util

import org.bukkit.configuration.file.FileConfiguration
import ru.astrainteractive.astralibs.util.hex
import ru.astrainteractive.klibs.kstorage.MutableStorageValue
import ru.astrainteractive.klibs.kstorage.api.MutableStorageValue
import kotlin.reflect.KProperty

class StringConfiguration(
    path: String,
    default: String,
    fc: FileConfiguration
) : MutableStorageValue<String> by MutableStorageValue(
    default = default,
    loadSettingsValue = {
        if (!fc.contains(path)) fc.set(path, default)
        fc.getString(path, default) ?: default
    },
    saveSettingsValue = {
        fc.set(path, it)
    }
)

class StringListConfiguration(
    path: String,
    default: List<String>,
    fc: FileConfiguration
) : MutableStorageValue<List<String>> by MutableStorageValue(
    default = default,
    loadSettingsValue = {
        if (!fc.contains(path)) fc.set(path, emptyList<String>())
        fc.getStringList(path).map { it.hex() }
    },
    saveSettingsValue = {
        fc.set(path, it)
    }
)

class BooleanConfiguration(
    path: String,
    default: Boolean,
    fc: FileConfiguration
) : MutableStorageValue<Boolean> by MutableStorageValue(
    default = default,
    loadSettingsValue = {
        if (!fc.contains(path)) fc.set(path, default)
        fc.getBoolean(path, default)
    },
    saveSettingsValue = {
        fc.set(path, it)
    }
)

class IntConfiguration(
    path: String,
    default: Int,
    fc: FileConfiguration
) : MutableStorageValue<Int> by MutableStorageValue(
    default = default,
    loadSettingsValue = {
        if (!fc.contains(path)) fc.set(path, default)
        fc.getInt(path, default)
    },
    saveSettingsValue = {
        fc.set(path, it)
    }
)

fun FileConfiguration.cString(path: String, default: String) = StringConfiguration(path, default, this)

fun FileConfiguration.cStringList(path: String) = StringListConfiguration(path, emptyList(), this)

fun FileConfiguration.cBoolean(path: String, default: Boolean) = BooleanConfiguration(path, default, this)

fun FileConfiguration.cInt(path: String, default: Int) = IntConfiguration(path, default, this)
inline operator fun <reified T, K> MutableStorageValue<T>.getValue(t: K?, property: KProperty<*>): T {
    return this.value
}
