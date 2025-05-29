package ru.astrainteractive.aspekt.module.tpa.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.asUnboxed
import ru.astrainteractive.aspekt.module.tpa.api.TpaApi
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.server.MinecraftNativeBridge
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class TpaCommandExecutor(
    translationKrate: CachedKrate<PluginTranslation>,
    private val tpaApi: TpaApi,
    private val scope: CoroutineScope,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    minecraftNativeBridge: MinecraftNativeBridge
) : CommandExecutor<TpaCommand>,
    MinecraftNativeBridge by minecraftNativeBridge,
    KyoriComponentSerializer by kyoriKrate.asUnboxed() {
    private val translation by translationKrate

    private suspend fun tpaCancel(input: TpaCommand.TpaCancel) {
        if (!tpaApi.isBeingWaited(input.executorPlayer)) {
            input.executorPlayer.asAudience().sendMessage(translation.tpa.youHaveNoPendingTp.component)
            return
        }
        tpaApi.cancel(input.executorPlayer)
        input.executorPlayer.asAudience().sendMessage(translation.tpa.requestCancelled.component)
    }

    private suspend fun tpaDeny(input: TpaCommand.TpaDeny) {
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

    private suspend fun tpaHere(input: TpaCommand.TpaHere) {
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

    private suspend fun tpaTo(input: TpaCommand.TpaTo) {
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

    private suspend fun tpaAccept(input: TpaCommand.TpaAccept) {
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
