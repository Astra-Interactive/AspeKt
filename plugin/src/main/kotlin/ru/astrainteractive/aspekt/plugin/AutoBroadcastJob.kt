package ru.astrainteractive.aspekt.plugin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.utils.ScheduledJob
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.utils.hex

class AutoBroadcastJob(
    config: Dependency<PluginConfiguration>,
    private val dispatchers: BukkitDispatchers,
    private val scope: CoroutineScope
) : ScheduledJob("AutoBroadcast") {
    private val config by config

    override val delayMillis: Long
        get() = config.announcements.interval.value * 1000L

    override val initialDelayMillis: Long
        get() = 0L

    override fun execute() {
        scope.launch(dispatchers.BukkitMain) {
            val message = config.announcements.announcements.value.randomOrNull()?.hex() ?: return@launch
            Bukkit.getOnlinePlayers().forEach {
                it.sendMessage(message)
            }
        }
    }
}
