package ru.astrainteractive.aspekt.module.playtimereward.service

import ru.astrainteractive.aspekt.module.playtimereward.controller.PlaytimeRewardController
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.service.ServiceExecutor

internal class PlaytimeServiceExecutor(
    private val playtimeRewardController: PlaytimeRewardController,
    private val platformServer: PlatformServer
) : ServiceExecutor {
    override suspend fun doWork() {
        platformServer.getOnlinePlayers().onEach { onlineKPlayer ->
            playtimeRewardController.checkPlaytime(onlineKPlayer)
        }
    }
}
