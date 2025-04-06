package ru.astrainteractive.aspekt.module.autobroadcast.job

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.autobroadcast.di.AutoBroadcastDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import ru.astrainteractive.aspekt.plugin.PluginConfiguration.Announcements.Announcement.BossBar.BarColor as AspektBarColor

internal class AutoBroadcastJob(
    dependencies: AutoBroadcastDependencies
) : ScheduledJob("AutoBroadcast"), AutoBroadcastDependencies by dependencies {
    override val delayMillis: Long
        get() = announcements.interval * 1000L

    override val initialDelayMillis: Long
        get() = 0L

    override val isEnabled: Boolean
        get() = true

    private fun AspektBarColor.toKyoriBossBar(): BossBar.Color {
        return when (this) {
            AspektBarColor.PINK -> BossBar.Color.PINK
            AspektBarColor.BLUE -> BossBar.Color.BLUE
            AspektBarColor.RED -> BossBar.Color.RED
            AspektBarColor.GREEN -> BossBar.Color.GREEN
            AspektBarColor.YELLOW -> BossBar.Color.YELLOW
            AspektBarColor.PURPLE -> BossBar.Color.PURPLE
            AspektBarColor.WHITE -> BossBar.Color.WHITE
        }
    }

    private var lastUsedBossBar: BossBar? = null
    private var i = Random.nextInt()

    override fun execute() {
        scope.launch(dispatchers.Main) {
            i++
            if (i >= Int.MAX_VALUE) i = 0
            val announcements = announcements.announcements.values.toList()
            if (announcements.isEmpty()) return@launch
            val announcement = announcements.getOrNull(i % announcements.size) ?: return@launch
            when (announcement) {
                is PluginConfiguration.Announcements.Announcement.ActionBar -> {
                    val message = announcement.text.let(kyoriComponentSerializer::toComponent)
                    Bukkit.getOnlinePlayers().forEach { player ->
                        player.sendActionBar(message)
                    }
                }

                is PluginConfiguration.Announcements.Announcement.BossBar -> {
                    val message = announcement.text.let(kyoriComponentSerializer::toComponent)
                    val bossBar = BossBar.bossBar(
                        message,
                        1f,
                        announcement.barColor.toKyoriBossBar(),
                        BossBar.Overlay.PROGRESS,
                    )
                    lastUsedBossBar = bossBar
                    Bukkit.getOnlinePlayers().forEach { player ->
                        player.showBossBar(bossBar)
                    }
                    launch(dispatchers.IO) {
                        val i = 10.milliseconds
                            .inWholeMilliseconds
                            .div(announcement.duration.inWholeMilliseconds.toFloat())
                        while (bossBar.progress() >= BossBar.MIN_PROGRESS) {
                            val newProgress = bossBar.progress().minus(i)
                            if (newProgress <= BossBar.MIN_PROGRESS) break
                            bossBar.progress(newProgress)
                            delay(10.milliseconds)
                        }
                        bossBar.viewers().filterIsInstance<Audience>().forEach(bossBar::removeViewer)
                        Bukkit.getOnlinePlayers().forEach(bossBar::removeViewer)
                        lastUsedBossBar = null
                    }
                }

                is PluginConfiguration.Announcements.Announcement.Text -> {
                    val message = announcement.text.let(kyoriComponentSerializer::toComponent)
                    Bukkit.getOnlinePlayers().forEach { player ->
                        player.sendMessage(message)
                    }
                }
            }
        }
    }

    override fun onDisable() {
        lastUsedBossBar?.let { bossBar ->
            bossBar.viewers().filterIsInstance<Audience>().forEach(bossBar::removeViewer)
        }
        super.onDisable()
    }
}
