package ru.astrainteractive.aspekt.module.tpa.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.minecraft.messenger.MinecraftMessenger
import ru.astrainteractive.aspekt.minecraft.teleport.TeleportApi
import ru.astrainteractive.aspekt.module.tpa.api.TpaApi
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.util.getValue

class TpaCommandExecutor(
    translationKrate: Krate<PluginTranslation>,
    private val teleportApi: TeleportApi,
    private val tpaApi: TpaApi,
    private val scope: CoroutineScope,
    private val messenger: MinecraftMessenger
) : CommandExecutor<TpaCommand> {
    private val translation by translationKrate

    private fun tpaCancel(input: TpaCommand.TpaCancel) {
        if (!tpaApi.isBeingWaited(input.executorPlayer.uuid)) {
            messenger.send(input.executorPlayer, translation.tpa.youHaveNoPendingTp)
            return
        }
        tpaApi.cancel(input.executorPlayer.uuid)
        messenger.send(input.executorPlayer, translation.tpa.requestCancelled)
    }

    private fun tpaDeny(input: TpaCommand.TpaDeny) {
        if (!tpaApi.isBeingWaited(input.executorPlayer.uuid)) {
            messenger.send(input.executorPlayer, translation.tpa.noPendingTpToDeny)
            return
        }
        tpaApi.deny(input.executorPlayer.uuid).forEach { deniedPlayerUuid ->
            messenger.send(
                deniedPlayerUuid,
                translation.tpa.requestDenied(input.executorPlayer.name)
            )
        }
        messenger.send(input.executorPlayer, translation.tpa.requestCancelled)
    }

    private fun tpaHere(input: TpaCommand.TpaHere) {
        if (input.executorPlayer.uuid == input.targetPlayer.uuid) {
            messenger.send(input.executorPlayer, translation.tpa.cantTpSelf)
            return
        }
        tpaApi.tpaHere(
            input.executorPlayer.uuid,
            input.targetPlayer.uuid
        )
        messenger.send(input.executorPlayer, translation.tpa.requestSent)
        messenger.send(
            input.targetPlayer,
            translation.tpa.requestTpaHere(input.executorPlayer.name)
        )
    }

    private fun tpaTo(input: TpaCommand.TpaTo) {
        if (input.executorPlayer.uuid == input.targetPlayer.uuid) {
            messenger.send(input.executorPlayer, translation.tpa.cantTpSelf)
            return
        }
        tpaApi.tpa(
            input.executorPlayer.uuid,
            input.targetPlayer.uuid
        )
        messenger.send(input.executorPlayer, translation.tpa.requestSent)
        messenger.send(
            input.targetPlayer,
            translation.tpa.requestTpa(input.executorPlayer.name)
        )
    }

    private fun tpaAccept(input: TpaCommand.TpaAccept) = scope.launch {
        if (!tpaApi.isBeingWaited(input.executorPlayer.uuid)) {
            messenger.send(input.executorPlayer, translation.tpa.noPendingTpToDeny)
            return@launch
        }
        val tpas = tpaApi.get(input.executorPlayer.uuid)
        tpas.forEach { (executorUuid, request) ->
            when (request.type) {
                TpaApi.RequestType.TPA -> {
                    teleportApi.teleport(
                        executorUuid,
                        request.uuid
                    )
                }

                TpaApi.RequestType.TPAHERE -> {
                    teleportApi.teleport(
                        request.uuid,
                        executorUuid,
                    )
                }
            }
        }
        messenger.send(input.executorPlayer, translation.tpa.requestAccepted)
    }

    override fun execute(input: TpaCommand) {
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
