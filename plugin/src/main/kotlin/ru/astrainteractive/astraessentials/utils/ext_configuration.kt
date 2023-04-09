package ru.astrainteractive.astraessentials.utils

import kotlinx.serialization.json.Json.Default.configuration
import org.bukkit.configuration.file.FileConfiguration
import ru.astrainteractive.astralibs.configuration.Configuration
import ru.astrainteractive.astralibs.configuration.DefaultConfiguration
import ru.astrainteractive.astralibs.utils.getFloat
import ru.astrainteractive.astralibs.utils.HEX
import kotlin.reflect.KProperty

class StringConfiguration(
    path: String,
    default: String,
    fc: FileConfiguration
) : Configuration<String> by DefaultConfiguration(
    default = default,
    load = {
        if (!fc.contains(path)) fc.set(path, default)
        fc.getString(path, default) ?: default
    },
    save = {
        fc.set(path, it)
    }
)

class StringListConfiguration(
    path: String,
    default: List<String>,
    fc: FileConfiguration
) : Configuration<List<String>> by DefaultConfiguration(
    default = default,
    load = {
        if (!fc.contains(path)) fc.set(path, emptyList<String>())
        fc.getStringList(path).map { it.HEX() }
    },
    save = {
        fc.set(path, it)
    }
)

class BooleanConfiguration(
    path: String,
    default: Boolean,
    fc: FileConfiguration
) : Configuration<Boolean> by DefaultConfiguration(
    default = default,
    load = {
        if (!fc.contains(path)) fc.set(path, default)
        fc.getBoolean(path, default)
    },
    save = {
        fc.set(path, it)
    }
)

class IntConfiguration(
    path: String,
    default: Int,
    fc: FileConfiguration
) : Configuration<Int> by DefaultConfiguration(
    default = default,
    load = {
        if (!fc.contains(path)) fc.set(path, default)
        fc.getInt(path, default)
    },
    save = {
        fc.set(path, it)
    }
)

fun FileConfiguration.cString(path: String, default: String) = StringConfiguration(path, default, this)

fun FileConfiguration.cStringList(path: String) = StringListConfiguration(path, emptyList(), this)

fun FileConfiguration.cBoolean(path: String, default: Boolean) = BooleanConfiguration(path, default, this)

fun FileConfiguration.cInt(path: String, default: Int) = IntConfiguration(path, default, this)
inline operator fun <reified T, K> Configuration<T>.getValue(t: K?, property: KProperty<*>): T {
    return this.value
}

