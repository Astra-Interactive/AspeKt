@file:OptIn(UnsafeApi::class)
package ru.astrainteractive.aspekt.modules

import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.aspekt.utils.ScheduledJob
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.di.Dependency

class AutoBroadcast(
    config: Dependency<PluginConfiguration>,
    private val bukkitDispatchers: BukkitDispatchers
) : ScheduledJob("AutoBroadcast") {
    private val config by config
    override val delayMillis: Long
        get() = config.announcements.interval.value * 1000L
    override val initialDelayMillis: Long
        get() = 0L

    override fun execute() {
        PluginScope.launch(bukkitDispatchers.BukkitMain) {
            val message = config.announcements.announcements.value.randomOrNull()?.HEX() ?: return@launch
            Bukkit.getOnlinePlayers().forEach {
                it.sendMessage(message)
            }
        }
    }
}
