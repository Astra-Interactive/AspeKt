package ru.astrainteractive.aspekt.module.autobroadcast.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.module.autobroadcast.model.AnnouncementsConfiguration
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.service.ServiceTask
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import ru.astrainteractive.aspekt.module.autobroadcast.model.AnnouncementsConfiguration.Announcement.BossBar.BarColor as AspektBarColor

internal class AutoBroadcastServiceTask(
    announcementsConfigKrate: CachedKrate<AnnouncementsConfiguration>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val ioScope: CoroutineScope,
    private val dispatchers: KotlinDispatchers
) : ServiceTask {
    private val kyori by kyoriKrate
    private val announcementsConfiguration by announcementsConfigKrate

    /**
     * Rotation cursor over the configured announcements. Starts at a random position so that
     * a server restart does not always replay the announcements in the same order.
     */
    private var announcementIndex: Int = Random.nextInt()

    /** Written from the drain coroutine, read from the lifecycle thread on shutdown. */
    @Volatile
    private var shownBossBar: BossBar? = null

    private fun AspektBarColor.toKyoriBossBarColor(): BossBar.Color {
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

    private fun hideBossBar(bossBar: BossBar) {
        bossBar.viewers().filterIsInstance<Audience>().forEach(bossBar::removeViewer)
        Bukkit.getOnlinePlayers().forEach(bossBar::removeViewer)
    }

    /**
     * Drains the boss bar progress from full to empty over [duration], then hides it.
     * Runs detached from the caller so that the broadcast interval is not extended by [duration].
     */
    private fun drainBossBarProgress(bossBar: BossBar, duration: Duration) {
        ioScope.launch(dispatchers.IO) {
            val progressStep = PROGRESS_STEP_DELAY.inWholeMilliseconds
                .div(duration.inWholeMilliseconds.toFloat())
            while (bossBar.progress() >= BossBar.MIN_PROGRESS) {
                val newProgress = bossBar.progress().minus(progressStep)
                if (newProgress <= BossBar.MIN_PROGRESS) break
                bossBar.progress(newProgress)
                delay(PROGRESS_STEP_DELAY)
            }
            hideBossBar(bossBar)
            shownBossBar = null
        }
    }

    private fun showBossBar(announcement: AnnouncementsConfiguration.Announcement.BossBar, message: Component) {
        val bossBar = BossBar.bossBar(
            message,
            1f,
            announcement.barColor.toKyoriBossBarColor(),
            BossBar.Overlay.PROGRESS,
        )
        shownBossBar = bossBar
        Bukkit.getOnlinePlayers().forEach { player -> player.showBossBar(bossBar) }
        drainBossBarProgress(bossBar = bossBar, duration = announcement.duration)
    }

    private fun broadcast(announcement: AnnouncementsConfiguration.Announcement) {
        val message = announcement.text.let(kyori::toComponent)
        when (announcement) {
            is AnnouncementsConfiguration.Announcement.ActionBar -> {
                Bukkit.getOnlinePlayers().forEach { player -> player.sendActionBar(message) }
            }

            is AnnouncementsConfiguration.Announcement.BossBar -> {
                showBossBar(announcement = announcement, message = message)
            }

            is AnnouncementsConfiguration.Announcement.Text -> {
                Bukkit.getOnlinePlayers().forEach { player -> player.sendMessage(message) }
            }
        }
    }

    private fun nextAnnouncement(): AnnouncementsConfiguration.Announcement? {
        val announcements = announcementsConfiguration.announcements.values.toList()
        if (announcements.isEmpty()) return null
        announcementIndex = announcementIndex.inc().mod(announcements.size)
        return announcements[announcementIndex]
    }

    /**
     * Hides the boss bar that is still being drained, if any. Must be called when the module
     * shuts down, otherwise the bar stays on screen until the player reconnects.
     */
    fun hideShownBossBar() {
        shownBossBar?.let(::hideBossBar)
        shownBossBar = null
    }

    override suspend fun execute() {
        val announcement = nextAnnouncement() ?: return
        withContext(dispatchers.Main) { broadcast(announcement) }
    }

    companion object {
        private val PROGRESS_STEP_DELAY = 10.milliseconds
    }
}
