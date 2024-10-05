package ru.astrainteractive.aspekt.module.autobroadcast.job

import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.autobroadcast.di.AutoBroadcastDependencies

internal class AutoBroadcastJob(
    dependencies: AutoBroadcastDependencies
) : ScheduledJob("AutoBroadcast"), AutoBroadcastDependencies by dependencies {
    override val delayMillis: Long
        get() = announcements.interval * 1000L

    override val initialDelayMillis: Long
        get() = 0L

    override val isEnabled: Boolean
        get() = true

    override fun execute() {
        scope.launch(dispatchers.BukkitMain) {
            val message = announcements.announcements.randomOrNull()
                ?.let(kyoriComponentSerializer::toComponent)
                ?: return@launch
            Bukkit.getOnlinePlayers().forEach {
                it.sendMessage(message)
            }
        }
    }
}
