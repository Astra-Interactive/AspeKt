package ru.astrainteractive.aspekt.module.playtimereward.usecase

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.astrainteractive.aspekt.module.playtimereward.krate.PlaytimeRewardKrate
import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeEvent
import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeRewardConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class TrackPlaytimeUseCase(
    private val clock: Clock,
    private val playtimeRewardKrateFactory: PlaytimeRewardKrate.Factory,
    private val economyFacade: EconomyFacade?,
    playtimeRewardConfigurationKrate: CachedKrate<PlaytimeRewardConfiguration>,
    pluginTranslationKrate: CachedKrate<PluginTranslation>
) {
    private val configuration by playtimeRewardConfigurationKrate
    private val translation by pluginTranslationKrate
    private val mutex = Mutex()

    private suspend fun rewardIfDue(player: OnlineKPlayer, krate: PlaytimeRewardKrate) {
        val playtimeReward = krate.saveAndGet { reward -> reward.countedUntil(clock.now()) }
        if (!playtimeReward.isRewardDue(configuration.requiredDuration)) return
        krate.reset()
        economyFacade?.addMoney(player.uuid, configuration.rewardAmount)
        player.sendMessage(translation.playtimeReward.rewarded(configuration.rewardAmount))
    }

    suspend operator fun invoke(player: OnlineKPlayer, event: PlaytimeEvent) {
        mutex.withLock {
            val krate = playtimeRewardKrateFactory.create(player)
            when (event) {
                PlaytimeEvent.Joined -> krate.saveAndGet { reward -> reward.joinedAt(clock.now()) }
                PlaytimeEvent.Left -> krate.saveAndGet { reward -> reward.countedUntil(clock.now()) }
                PlaytimeEvent.Checked -> rewardIfDue(player, krate)
            }
        }
    }
}
