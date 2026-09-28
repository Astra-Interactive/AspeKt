package ru.astrainteractive.aspekt.module.rtp.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.module.rtp.api.RtpSearchResult
import ru.astrainteractive.aspekt.module.rtp.api.SafeLocationProvider
import ru.astrainteractive.aspekt.module.rtp.model.RtpConfig
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RtpCommandExecutor(
    private val ioScope: CoroutineScope,
    private val safeLocationProvider: SafeLocationProvider,
    private val dispatchers: KotlinDispatchers,
    translationKrate: CachedKrate<PluginTranslation>,
    rtpConfigKrate: CachedKrate<RtpConfig>,
) {
    private val translation by translationKrate
    private val rtpConfig by rtpConfigKrate

    fun execute(input: RtpCommand) {
        ioScope.launch {
            val player = input.player
            if (safeLocationProvider.getJobsNumber() >= rtpConfig.maxSearchJobs) {
                player.sendMessage(translation.rtp.maxJobs)
                return@launch
            }
            if (safeLocationProvider.isActive(player.uuid)) return@launch
            @Suppress("MagicNumber")
            if (input.nextTickTime < 18) {
                player.sendMessage(translation.rtp.lowTickTime(input.nextTickTime))
                return@launch
            }
            if (safeLocationProvider.hasTimeout(player.uuid)) {
                player.sendMessage(translation.rtp.timeout)
                return@launch
            }
            player.sendMessage(translation.rtp.searching)
            when (val result = safeLocationProvider.getLocation(this@launch, player.uuid)) {
                is RtpSearchResult.Success -> {
                    player.sendMessage(translation.rtp.foundPlace)
                    withContext(dispatchers.Main) {
                        player.teleport(result.location)
                    }
                }

                RtpSearchResult.MaxRetriesReached -> {
                    player.sendMessage(translation.rtp.maxRetries)
                }

                RtpSearchResult.NotFound -> {
                    player.sendMessage(translation.rtp.notFoundPlace)
                }
            }
        }
    }
}
