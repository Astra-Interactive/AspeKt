package ru.astrainteractive.aspekt.module.playtimereward.controller

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.astrainteractive.aspekt.module.playtimereward.krate.PlaytimeRewardKrate
import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeReward
import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeRewardConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.MutableKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class PlaytimeRewardController(
    private val clock: Clock = Clock.System,
    private val playtimeRewardKrateFactory: PlaytimeRewardKrate.Factory,
    private val economyFacade: EconomyFacade?,
    playtimeRewardConfigurationKrate: CachedKrate<PlaytimeRewardConfiguration>,
    pluginTranslationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>
) : Logger by JUtiltLogger("AspeKt-PlaytimeRewardController"),
    KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val playtimeRewardConfiguration by playtimeRewardConfigurationKrate
    private val pluginTranslation by pluginTranslationKrate
    private val mutex = Mutex()
    suspend fun onPlayerJoin(onlineKPlayer: OnlineKPlayer) {
        mutex.withLock {
            val playtimeRewardStoreKrate = playtimeRewardKrateFactory.create(onlineKPlayer)
            playtimeRewardStoreKrate.saveAndGet { playtimeReward ->
                playtimeReward.copy(latestJoin = clock.now())
            }
        }
    }

    private fun MutableKrate<PlaytimeReward>.increasePlayTime() {
        saveAndGet { playtimeReward ->
            val timeDiff = clock.now().minus(playtimeReward.latestJoin)
            playtimeReward.copy(
                latestJoin = clock.now(),
                timePlayed = playtimeReward.timePlayed + timeDiff
            )
        }
    }

    suspend fun onPlayerQuit(onlineKPlayer: OnlineKPlayer) {
        mutex.withLock {
            val playtimeRewardStoreKrate = playtimeRewardKrateFactory.create(onlineKPlayer)
            playtimeRewardStoreKrate.increasePlayTime()
        }
    }

    suspend fun checkPlaytime(onlineKPlayer: OnlineKPlayer) {
        mutex.withLock {
            val playtimeRewardStoreKrate = playtimeRewardKrateFactory.create(onlineKPlayer)
            playtimeRewardStoreKrate.increasePlayTime()
            val timePlayed = playtimeRewardStoreKrate.getValue().timePlayed
            if (timePlayed < playtimeRewardConfiguration.requiredDuration) return
            playtimeRewardStoreKrate.reset()
            economyFacade?.addMoney(onlineKPlayer.uuid, playtimeRewardConfiguration.rewardAmount)
            pluginTranslation.playtimeReward.rewarded(playtimeRewardConfiguration.rewardAmount)
                .component
                .run(onlineKPlayer::sendMessage)
        }
    }
}
