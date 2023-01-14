package ru.astrainteractive.astraessentials.modules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astraessentials.utils.ScheduledJob

object AutoBroadcast : ScheduledJob("AutoBroadcast") {
    private val config by PluginConfigModule
    override val delayMillis: Long
        get() = config.announcements.interval.value * 1000L
    override val initialDelayMillis: Long
        get() = 0L

    override fun execute() {
        PluginScope.launch(Dispatchers.BukkitMain) {
            val message = config.announcements.announcements.value.randomOrNull()?.HEX() ?: return@launch
            Bukkit.getOnlinePlayers().forEach {
                it.sendMessage(message)
            }
        }
    }
}
