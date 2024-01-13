package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.util.adminChunk
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.command.api.CommandExecutor
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer

internal class AdminPrivateCommandExecutor(
    private val adminPrivateController: AdminPrivateController,
    private val scope: CoroutineScope,
    private val translation: PluginTranslation,
    private val dispatchers: BukkitDispatchers,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) : CommandExecutor<AdminPrivateCommand.Input> {

    private suspend fun showMap(player: Player) {
        val result = runCatching {
            adminPrivateController.map(5, player.chunk.adminChunk)
        }
        result.onSuccess {
            translation.adminPrivate.blockMap
                .let(kyoriComponentSerializer::toComponent)
                .run(player::sendMessage)
            it.forEach {
                it.joinToString("") { if (it) "&#1cba56☒" else "&#c91e1e☒" }
                    .let(KyoriComponentSerializer.Legacy::toComponent)
                    .run(player::sendMessage)
            }
        }
        result.onFailure {
            it.printStackTrace()
            translation.adminPrivate.error
                .let(kyoriComponentSerializer::toComponent)
                .run(player::sendMessage)
        }
    }

    private suspend fun setFlag(input: AdminPrivateCommand.Input.SetFlag) {
        val result = runCatching {
            adminPrivateController.setFlag(
                flag = input.flag,
                value = input.value,
                chunk = input.player.chunk.adminChunk
            )
        }
        result.onSuccess {
            translation.adminPrivate.chunkFlagChanged
                .let(kyoriComponentSerializer::toComponent)
                .run(input.player::sendMessage)
        }
        result.onFailure {
            it.printStackTrace()
            translation.adminPrivate.error
                .let(kyoriComponentSerializer::toComponent)
                .run(input.player::sendMessage)
        }
    }

    private suspend fun claim(input: AdminPrivateCommand.Input.Claim) {
        val result = runCatching {
            adminPrivateController.claim(input.player.chunk.adminChunk)
        }
        result.onSuccess {
            translation.adminPrivate.chunkClaimed
                .let(kyoriComponentSerializer::toComponent)
                .run(input.player::sendMessage)
        }
        result.onFailure {
            it.printStackTrace()
            translation.adminPrivate.error
                .let(kyoriComponentSerializer::toComponent)
                .run(input.player::sendMessage)
        }
    }

    private suspend fun unclaim(input: AdminPrivateCommand.Input.UnClaim) {
        val result = runCatching {
            adminPrivateController.unclaim(input.player.chunk.adminChunk)
        }
        result.onSuccess {
            translation.adminPrivate.chunkUnClaimed
                .let(kyoriComponentSerializer::toComponent)
                .run(input.player::sendMessage)
        }
        result.onFailure {
            it.printStackTrace()
            translation.adminPrivate.error
                .let(kyoriComponentSerializer::toComponent)
                .run(input.player::sendMessage)
        }
    }

    override fun execute(input: AdminPrivateCommand.Input) {
        when (input) {
            is AdminPrivateCommand.Input.Claim -> scope.launch(dispatchers.IO) {
                claim(input)
            }

            is AdminPrivateCommand.Input.SetFlag -> scope.launch(dispatchers.IO) {
                setFlag(input)
            }

            is AdminPrivateCommand.Input.ShowMap -> scope.launch(dispatchers.IO) {
                showMap(input.player)
            }

            is AdminPrivateCommand.Input.UnClaim -> scope.launch(dispatchers.IO) {
                unclaim(input)
            }
        }
    }
}
