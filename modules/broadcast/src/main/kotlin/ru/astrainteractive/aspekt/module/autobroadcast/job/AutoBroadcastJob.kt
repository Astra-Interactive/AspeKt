package ru.astrainteractive.aspekt.module.autobroadcast.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.autobroadcast.model.AnnouncementsConfiguration
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import ru.astrainteractive.aspekt.module.autobroadcast.model.AnnouncementsConfiguration.Announcement.BossBar.BarColor as AspektBarColor

internal class AutoBroadcastJob(
    val configKrate: CachedKrate<AnnouncementsConfiguration>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    val ioScope: CoroutineScope,
    val dispatchers: KotlinDispatchers
) : ScheduledJob("AutoBroadcast") {
    private val kyori by kyoriKrate
    val announcementsConfiguration by configKrate

    @Suppress("MagicNumber")
    override val delayMillis: Long
        get() = announcementsConfiguration.interval * 1000L

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
        ioScope.launch(dispatchers.Main) {
            i++
            if (i >= Int.MAX_VALUE) i = 0
            val announcements = announcementsConfiguration.announcements.values.toList()
            if (announcements.isEmpty()) return@launch
            val announcement = announcements.getOrNull(i % announcements.size) ?: return@launch
            when (announcement) {
                is AnnouncementsConfiguration.Announcement.ActionBar -> {
                    val message = announcement.text.let(kyori::toComponent)
                    Bukkit.getOnlinePlayers().forEach { player ->
                        player.sendActionBar(message)
                    }
                }

                is AnnouncementsConfiguration.Announcement.BossBar -> {
                    val message = announcement.text.let(kyori::toComponent)
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

                is AnnouncementsConfiguration.Announcement.Text -> {
                    val message = announcement.text.let(kyori::toComponent)
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
