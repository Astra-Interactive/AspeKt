package ru.astrainteractive.aspekt.module.rtp.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.minecraft.messenger.MinecraftMessenger
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.teleport.TeleportApi
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RtpCommandExecutor(
    private val scope: CoroutineScope,
    private val messenger: MinecraftMessenger,
    private val safeLocationProvider: SafeLocationProvider,
    private val teleportApi: TeleportApi,
    private val dispatchers: KotlinDispatchers,
    translationKrate: Krate<PluginTranslation>
) : CommandExecutor<RtpCommand> {
    private val translation by translationKrate
    override fun execute(input: RtpCommand) {
        val player = input.player
        if (safeLocationProvider.getJobsNumber() > 0) {
            messenger.send(player.uuid, translation.rtp.maxRtpJobs)
            return
        }
        if (safeLocationProvider.isActive(player.uuid)) return
        if (input.averageTickTime < 18) {
            messenger.send(player.uuid, translation.rtp.lowTickTime(input.averageTickTime))
            return
        }
        if (safeLocationProvider.hasTimeout(player.uuid)) {
            messenger.send(player.uuid, translation.rtp.timeout)
            return
        }
        messenger.send(player.uuid, translation.rtp.searching)
        scope.launch {
            val location = safeLocationProvider.getLocation(this, player.uuid)
            if (location == null) {
                messenger.send(player.uuid, translation.rtp.notFoundPlace)
                return@launch
            }
            messenger.send(player.uuid, translation.rtp.foundPlace)
            withContext(dispatchers.Main) {
                teleportApi.teleport(OnlineMinecraftPlayer(player.uuid, player.name), location)
            }
        }
    }
}
