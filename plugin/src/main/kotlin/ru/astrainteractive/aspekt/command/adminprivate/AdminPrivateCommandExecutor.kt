package ru.astrainteractive.aspekt.command.adminprivate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.adminprivate.util.adminChunk
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.command.api.CommandExecutor
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.util.hex

internal class AdminPrivateCommandExecutor(
    private val adminPrivateController: AdminPrivateController,
    private val scope: CoroutineScope,
    private val translation: PluginTranslation,
    private val dispatchers: BukkitDispatchers,
    translationContext: BukkitTranslationContext
) : CommandExecutor<AdminPrivateCommand.Input>,
    BukkitTranslationContext by translationContext {

    private suspend fun showMap(player: Player) {
        val result = runCatching {
            adminPrivateController.map(5, player.chunk.adminChunk)
        }
        result.onSuccess {
            player.sendMessage(translation.adminPrivate.blockMap)
            it.forEach {
                it.joinToString("") { if (it) "#1cba56☒".hex() else "#c91e1e☒".hex() }
                    .run(player::sendMessage)
            }
        }
        result.onFailure {
            it.printStackTrace()
            player.sendMessage(translation.adminPrivate.error)
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
            input.player.sendMessage(translation.adminPrivate.chunkFlagChanged)
        }
        result.onFailure {
            it.printStackTrace()
            input.player.sendMessage(translation.adminPrivate.error)
        }
    }

    private suspend fun claim(input: AdminPrivateCommand.Input.Claim) {
        val result = runCatching {
            adminPrivateController.claim(input.player.chunk.adminChunk)
        }
        result.onSuccess {
            input.player.sendMessage(translation.adminPrivate.chunkClaimed)
        }
        result.onFailure {
            it.printStackTrace()
            input.player.sendMessage(translation.adminPrivate.error)
        }
    }

    private suspend fun unclaim(input: AdminPrivateCommand.Input.UnClaim) {
        val result = runCatching {
            adminPrivateController.unclaim(input.player.chunk.adminChunk)
        }
        result.onSuccess {
            input.player.sendMessage(translation.adminPrivate.chunkUnClaimed)
        }
        result.onFailure {
            it.printStackTrace()
            input.player.sendMessage(translation.adminPrivate.error)
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
