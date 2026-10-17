package ru.astrainteractive.aspekt.module.playtimereward.service

import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeEvent
import ru.astrainteractive.aspekt.module.playtimereward.usecase.TrackPlaytimeUseCase
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.service.ServiceTask

internal class PlaytimeServiceTask(
    private val trackPlaytimeUseCase: TrackPlaytimeUseCase,
    private val platformServer: PlatformServer
) : ServiceTask {
    override suspend fun execute() {
        platformServer.getOnlinePlayers().onEach { onlineKPlayer ->
            trackPlaytimeUseCase.invoke(onlineKPlayer, PlaytimeEvent.Checked)
        }
    }
}
