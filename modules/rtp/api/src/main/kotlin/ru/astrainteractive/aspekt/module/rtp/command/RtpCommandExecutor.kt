package ru.astrainteractive.aspekt.module.rtp.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.module.rtp.api.RtpSearchResult
import ru.astrainteractive.aspekt.module.rtp.api.SafeLocationProvider
import ru.astrainteractive.aspekt.module.rtp.model.RtpConfig
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RtpCommandExecutor(
    private val ioScope: CoroutineScope,
    private val safeLocationProvider: SafeLocationProvider,
    private val dispatchers: KotlinDispatchers,
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    rtpConfigKrate: CachedKrate<RtpConfig>,
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate
    private val rtpConfig by rtpConfigKrate

    fun execute(input: RtpCommand) {
        ioScope.launch {
            val player = input.player
            if (safeLocationProvider.getJobsNumber() >= rtpConfig.maxSearchJobs) {
                player.sendMessage(translation.rtp.maxRtpJobs.component)
                return@launch
            }
            if (safeLocationProvider.isActive(player.uuid)) return@launch
            @Suppress("MagicNumber")
            if (input.nextTickTime < 18) {
                player.sendMessage(translation.rtp.lowTickTime(input.nextTickTime).component)
                return@launch
            }
            if (safeLocationProvider.hasTimeout(player.uuid)) {
                player.sendMessage(translation.rtp.timeout.component)
                return@launch
            }
            player.sendMessage(translation.rtp.searching.component)
            when (val result = safeLocationProvider.getLocation(this@launch, player.uuid)) {
                is RtpSearchResult.Success -> {
                    player.sendMessage(translation.rtp.foundPlace.component)
                    withContext(dispatchers.Main) {
                        player.teleport(result.location)
                    }
                }

                RtpSearchResult.MaxRetriesReached -> {
                    player.sendMessage(translation.rtp.maxRtpRetries.component)
                }

                RtpSearchResult.NotFound -> {
                    player.sendMessage(translation.rtp.notFoundPlace.component)
                }
            }
        }
    }
}
