package ru.astrainteractive.aspekt.module.tpa.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.minecraft.asAudience
import ru.astrainteractive.aspekt.minecraft.asLocatable
import ru.astrainteractive.aspekt.minecraft.asTeleportable
import ru.astrainteractive.aspekt.module.tpa.api.TpaApi
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.util.getValue

class TpaCommandExecutor(
    translationKrate: Krate<PluginTranslation>,
    private val tpaApi: TpaApi,
    private val scope: CoroutineScope,
    private val kyoriKrate: Krate<KyoriComponentSerializer>
) : CommandExecutor<TpaCommand> {
    private val translation by translationKrate
    private val kyori by kyoriKrate

    private suspend fun tpaCancel(input: TpaCommand.TpaCancel) = with(kyori) {
        if (!tpaApi.isBeingWaited(input.executorPlayer)) {
            input.executorPlayer.asAudience().sendMessage(translation.tpa.youHaveNoPendingTp.component)
            return
        }
        tpaApi.cancel(input.executorPlayer)
        input.executorPlayer.asAudience().sendMessage(translation.tpa.requestCancelled.component)
    }

    private suspend fun tpaDeny(input: TpaCommand.TpaDeny) = with(kyori) {
        if (!tpaApi.isBeingWaited(input.executorPlayer)) {
            input.executorPlayer
                .asAudience()
                .sendMessage(translation.tpa.noPendingTpToDeny.component)
            return
        }
        tpaApi.deny(input.executorPlayer).forEach { deniedPlayerUuid ->
            deniedPlayerUuid
                .asAudience()
                .sendMessage(translation.tpa.requestDenied(input.executorPlayer.name).component)
        }
        input.executorPlayer
            .asAudience()
            .sendMessage(translation.tpa.requestCancelled.component)
    }

    private suspend fun tpaHere(input: TpaCommand.TpaHere): Unit = with(kyori) {
        if (input.executorPlayer.uuid == input.targetPlayer.uuid) {
            input.executorPlayer
                .asAudience()
                .sendMessage(translation.tpa.cantTpSelf.component)
            return
        }
        tpaApi.tpaHere(
            input.executorPlayer,
            input.targetPlayer
        )
        input.executorPlayer
            .asAudience()
            .sendMessage(translation.tpa.requestSent.component)
        input.targetPlayer
            .asAudience()
            .sendMessage(translation.tpa.requestTpaHere(input.executorPlayer.name).component)
    }

    private suspend fun tpaTo(input: TpaCommand.TpaTo): Unit = with(kyori) {
        if (input.executorPlayer.uuid == input.targetPlayer.uuid) {
            input.executorPlayer
                .asAudience()
                .sendMessage(translation.tpa.cantTpSelf.component)
            return
        }
        tpaApi.tpa(
            input.executorPlayer,
            input.targetPlayer
        )
        input.executorPlayer
            .asAudience()
            .sendMessage(translation.tpa.requestSent.component)

        input.targetPlayer
            .asAudience()
            .sendMessage(translation.tpa.requestTpa(input.executorPlayer.name).component)
    }

    private suspend fun tpaAccept(input: TpaCommand.TpaAccept): Unit = with(kyori) {
        if (!tpaApi.isBeingWaited(input.executorPlayer)) {
            input.executorPlayer
                .asAudience()
                .sendMessage(translation.tpa.noPendingTpToDeny.component)
            return
        }
        val tpas = tpaApi.get(input.executorPlayer)
        tpas.forEach { (executorUuid, request) ->
            when (request.type) {
                TpaApi.RequestType.TPA -> {
                    executorUuid
                        .asTeleportable()
                        .teleport(request.player.asLocatable().getLocation())
                }

                TpaApi.RequestType.TPAHERE -> {
                    request.player
                        .asTeleportable()
                        .teleport(request.player.asLocatable().getLocation())
                }
            }
        }

        input.executorPlayer
            .asAudience()
            .sendMessage(translation.tpa.requestAccepted.component)
    }

    override fun execute(input: TpaCommand) {
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
