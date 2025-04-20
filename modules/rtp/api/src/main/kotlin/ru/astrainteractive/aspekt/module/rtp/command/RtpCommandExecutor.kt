package ru.astrainteractive.aspekt.module.rtp.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.minecraft.messenger.MinecraftMessenger
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.teleport.TeleportApi
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RtpCommandExecutor(
    private val scope: CoroutineScope,
    private val messenger: MinecraftMessenger,
    private val safeLocationProvider: SafeLocationProvider,
    private val teleportApi: TeleportApi,
    private val dispatchers: KotlinDispatchers
) : CommandExecutor<RtpCommand> {
    override fun execute(input: RtpCommand) {
        val player = input.player
        if (safeLocationProvider.getJobsNumber() > 0) {
            messenger.send(player.uuid, StringDesc.Raw("Max RTP jobs are reached!"))
            return
        }
        if (safeLocationProvider.isActive(player.uuid)) return
        if (safeLocationProvider.hasTimeout(player.uuid)) {
            messenger.send(player.uuid, StringDesc.Raw("Timeout..."))
            return
        }
        scope.launch {
            val location = safeLocationProvider.getLocation(this, player.uuid) ?: return@launch
            messenger.send(player.uuid, StringDesc.Raw("Found place for you!"))
            withContext(dispatchers.Main) {
                teleportApi.teleport(OnlineMinecraftPlayer(player.uuid, player.name), location)
            }
        }
    }
}
