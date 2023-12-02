package ru.astrainteractive.aspekt.autobroadcast

import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.autobroadcast.di.AutoBroadcastDependencies
import ru.astrainteractive.astralibs.util.hex
import ru.astrainteractive.klibs.kdi.getValue

internal class AutoBroadcastJob(
    dependencies: AutoBroadcastDependencies
) : ScheduledJob("AutoBroadcast"), AutoBroadcastDependencies by dependencies {
    override val delayMillis: Long
        get() = configuration.interval * 1000L

    override val initialDelayMillis: Long
        get() = 0L

    override fun execute() {
        scope.launch(dispatchers.BukkitMain) {
            val message = configuration.announcements.randomOrNull()?.hex() ?: return@launch
            Bukkit.getOnlinePlayers().forEach {
                it.sendMessage(message)
            }
        }
    }
}
