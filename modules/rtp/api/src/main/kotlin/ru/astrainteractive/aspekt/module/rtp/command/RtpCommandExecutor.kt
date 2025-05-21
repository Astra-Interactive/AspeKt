package ru.astrainteractive.aspekt.module.rtp.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.asUnboxed
import ru.astrainteractive.aspekt.minecraft.MinecraftNativeBridge
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RtpCommandExecutor(
    private val scope: CoroutineScope,
    private val safeLocationProvider: SafeLocationProvider,
    private val dispatchers: KotlinDispatchers,
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    minecraftNativeBridge: MinecraftNativeBridge
) : CommandExecutor<RtpCommand>,
    MinecraftNativeBridge by minecraftNativeBridge,
    KyoriComponentSerializer by kyoriKrate.asUnboxed() {
    private val translation by translationKrate
    override fun execute(input: RtpCommand) {
        scope.launch {
            val player = input.player
            if (safeLocationProvider.getJobsNumber() > 0) {
                player.asAudience().sendMessage(translation.rtp.maxRtpJobs.component)
                return@launch
            }
            if (safeLocationProvider.isActive(player.uuid)) return@launch
            if (input.nextTickTime < 18) {
                player.asAudience().sendMessage(translation.rtp.lowTickTime(input.nextTickTime).component)
                return@launch
            }
            if (safeLocationProvider.hasTimeout(player.uuid)) {
                player.asAudience().sendMessage(translation.rtp.timeout.component)
                return@launch
            }
            player.asAudience().sendMessage(translation.rtp.searching.component)
            val location = safeLocationProvider.getLocation(this@launch, player.uuid)
            if (location == null) {
                player.asAudience().sendMessage(translation.rtp.notFoundPlace.component)
                return@launch
            }
            player
                .asAudience()
                .sendMessage(translation.rtp.foundPlace.component)
            withContext(dispatchers.Main) {
                player
                    .asTeleportable()
                    .teleport(location)
            }
        }
    }
}
