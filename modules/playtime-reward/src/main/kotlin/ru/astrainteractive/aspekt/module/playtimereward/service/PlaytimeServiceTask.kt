package ru.astrainteractive.aspekt.module.playtimereward.service

import ru.astrainteractive.aspekt.module.playtimereward.controller.PlaytimeRewardController
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.service.ServiceTask

internal class PlaytimeServiceTask(
    private val playtimeRewardController: PlaytimeRewardController,
    private val platformServer: PlatformServer
) : ServiceTask {
    override suspend fun execute() {
        platformServer.getOnlinePlayers().onEach { onlineKPlayer ->
            playtimeRewardController.checkPlaytime(onlineKPlayer)
        }
    }
}
