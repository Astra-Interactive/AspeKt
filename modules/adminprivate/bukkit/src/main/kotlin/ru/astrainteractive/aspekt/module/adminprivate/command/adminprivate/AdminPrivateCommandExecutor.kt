package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.adminprivate.command.di.AdminPrivateCommandDependencies
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.adminprivate.util.claimChunk
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer

internal class AdminPrivateCommandExecutor(
    dependencies: AdminPrivateCommandDependencies
) : AdminPrivateCommandDependencies by dependencies,
    CommandExecutor<AdminPrivateCommand.Model> {

    private suspend fun showMap(claimPlayer: ClaimPlayer, chunk: ClaimChunk) {
        val result = runCatching {
            adminPrivateController.map(5, chunk)
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

    private suspend fun setFlag(input: AdminPrivateCommand.Model.SetFlag) {
        val result = runCatching {
            adminPrivateController.setFlag(
                flag = input.flag,
                value = input.value,
                chunk = input.chunk,
                claimPlayer = input.claimPlayer
            )
        }
        result.onSuccess {
            translation.adminPrivate.chunkFlagChanged
                .let(kyoriComponentSerializer::toComponent)
                .run(input.claimPlayer::sendMessage)
        }
        result.onFailure {
            it.printStackTrace()
            translation.adminPrivate.error
                .let(kyoriComponentSerializer::toComponent)
                .run(input.claimPlayer::sendMessage)
        }
    }

    private suspend fun claim(input: AdminPrivateCommand.Model.Claim) {
        val result = runCatching {
            adminPrivateController.claim(input.claimPlayer, input.chunk)
        }
        result.onSuccess {
            translation.adminPrivate.chunkClaimed
                .let(kyoriComponentSerializer::toComponent)
                .run(input.claimPlayer::sendMessage)
        }
        result.onFailure {
            it.printStackTrace()
            translation.adminPrivate.error
                .let(kyoriComponentSerializer::toComponent)
                .run(input.claimPlayer::sendMessage)
        }
    }

    private suspend fun unclaim(input: AdminPrivateCommand.Model.UnClaim) {
        val result = runCatching {
            adminPrivateController.unclaim(input.claimPlayer, input.chunk)
        }
        result.onSuccess {
            translation.adminPrivate.chunkUnClaimed
                .let(kyoriComponentSerializer::toComponent)
                .run(input.claimPlayer::sendMessage)
        }
        result.onFailure {
            it.printStackTrace()
            translation.adminPrivate.error
                .let(kyoriComponentSerializer::toComponent)
                .run(input.claimPlayer::sendMessage)
        }
    }

    override fun execute(input: AdminPrivateCommand.Model) {
        when (input) {
            is AdminPrivateCommand.Model.Claim -> scope.launch(dispatchers.IO) {
                claim(input)
            }

            is AdminPrivateCommand.Model.SetFlag -> scope.launch(dispatchers.IO) {
                setFlag(input)
            }

            is AdminPrivateCommand.Model.ShowMap -> scope.launch(dispatchers.IO) {
                showMap(input.claimPlayer, input.chunk)
            }

            is AdminPrivateCommand.Model.UnClaim -> scope.launch(dispatchers.IO) {
                unclaim(input)
            }
        }
    }
}
