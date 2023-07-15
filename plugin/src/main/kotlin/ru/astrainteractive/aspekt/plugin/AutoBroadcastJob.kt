package ru.astrainteractive.aspekt.plugin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.util.ScheduledJob
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.utils.hex
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.getValue

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
