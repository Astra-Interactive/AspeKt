package ru.astrainteractive.aspekt.module.tpa.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.tpa.api.TpaApi
import ru.astrainteractive.aspekt.module.tpa.model.TpaApiRequestType
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class TpaCommandExecutor(
    translationKrate: CachedKrate<PluginTranslation>,
    private val tpaApi: TpaApi,
    private val scope: CoroutineScope,
    private val platformServer: PlatformServer
) {
    private val translation by translationKrate

    private suspend fun tpaCancel(input: TpaCommand.TpaCancel) {
        if (!tpaApi.isBeingWaited(input.executorPlayer.uuid)) {
            input.executorPlayer.sendMessage(translation.tpa.youHaveNoPendingTp)
            return
        }
        tpaApi.cancel(input.executorPlayer.uuid)
        input.executorPlayer.sendMessage(translation.tpa.requestCancelled)
    }

    private suspend fun tpaDeny(input: TpaCommand.TpaDeny) {
        if (!tpaApi.isBeingWaited(input.executorPlayer.uuid)) {
            input.executorPlayer
                .sendMessage(translation.tpa.noPendingTpToDeny)
            return
        }
        tpaApi.deny(input.executorPlayer.uuid).forEach { deniedUuid ->
            platformServer.findOnlinePlayer(deniedUuid)
                ?.sendMessage(translation.tpa.requestDenied(input.executorPlayer.name))
        }
        input.executorPlayer
            .sendMessage(translation.tpa.requestCancelled)
    }

    private suspend fun tpaHere(input: TpaCommand.TpaHere) {
        if (input.executorPlayer.uuid == input.targetPlayer.uuid) {
            input.executorPlayer
                .sendMessage(translation.tpa.cantTpSelf)
            return
        }
        tpaApi.tpaHere(
            input.executorPlayer.uuid,
            input.targetPlayer.uuid
        )
        input.executorPlayer
            .sendMessage(translation.tpa.requestSent)
        input.targetPlayer
            .sendMessage(translation.tpa.requestTpaHere(input.executorPlayer.name))
    }

    private suspend fun tpaTo(input: TpaCommand.TpaTo) {
        if (input.executorPlayer.uuid == input.targetPlayer.uuid) {
            input.executorPlayer
                .sendMessage(translation.tpa.cantTpSelf)
            return
        }
        tpaApi.tpa(
            input.executorPlayer.uuid,
            input.targetPlayer.uuid
        )
        input.executorPlayer
            .sendMessage(translation.tpa.requestSent)
        input.targetPlayer
            .sendMessage(translation.tpa.requestTpa(input.executorPlayer.name))
    }

    private suspend fun tpaAccept(input: TpaCommand.TpaAccept) {
        if (!tpaApi.isBeingWaited(input.executorPlayer.uuid)) {
            input.executorPlayer
                .sendMessage(translation.tpa.noPendingTpToDeny)
            return
        }
        val tpas = tpaApi.get(input.executorPlayer.uuid)
        tpas.forEach { (executorUuid, request) ->
            val executor = platformServer.findOnlinePlayer(executorUuid) ?: return@forEach
            val target = platformServer.findOnlinePlayer(request.targetUuid) ?: return@forEach
            when (request.type) {
                TpaApiRequestType.TPA -> {
                    executor.teleport(target.getLocation())
                }

                TpaApiRequestType.TPAHERE -> {
                    target.teleport(executor.getLocation())
                }
            }
        }

        input.executorPlayer
            .sendMessage(translation.tpa.requestAccepted)
    }

    fun execute(input: TpaCommand) {
        scope.launch {
            when (input) {
                is TpaCommand.TpaCancel -> {
                    tpaCancel(input)
                }

                is TpaCommand.TpaDeny -> {
                    tpaDeny(input)
                }

                is TpaCommand.TpaHere -> {
                    tpaHere(input)
                }

                is TpaCommand.TpaTo -> {
                    tpaTo(input)
                }

                is TpaCommand.TpaAccept -> {
                    tpaAccept(input)
                }
            }
        }
    }
}
