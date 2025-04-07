package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.adminprivate.command.di.AdminPrivateCommandDependencies
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.messenger.Messenger
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.adminprivate.util.claimChunk
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class AdminPrivateCommandExecutor(
    private val messenger: Messenger,
    private val adminPrivateController: AdminPrivateController,
    private val scope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    translationKrate: Krate<PluginTranslation>,
) : CommandExecutor<AdminPrivateCommand.Model> {
    val translation by translationKrate

    private suspend fun showMap(claimPlayer: ClaimPlayer, chunk: ClaimChunk) {
        val result = runCatching {
            adminPrivateController.map(5, chunk)
        }
        result.onSuccess { claims ->
            messenger.sendMessage(claimPlayer, translation.adminPrivate.blockMap)
            claims.forEach { claim ->
                val desc = claim.joinToString("") { if (it) "&#1cba56☒" else "&#c91e1e☒" }
                    .let(StringDesc::Raw)
                messenger.sendMessage(claimPlayer, desc)
            }
        }
        result.onFailure {
            it.printStackTrace()
            messenger.sendMessage(claimPlayer, translation.adminPrivate.error)
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
            messenger.sendMessage(input.claimPlayer, translation.adminPrivate.chunkFlagChanged)
        }
        result.onFailure {
            it.printStackTrace()
            messenger.sendMessage(input.claimPlayer, translation.adminPrivate.error)
        }
    }

    private suspend fun claim(input: AdminPrivateCommand.Model.Claim) {
        val result = runCatching {
            adminPrivateController.claim(input.claimPlayer, input.chunk)
        }
        result.onSuccess {
            messenger.sendMessage(input.claimPlayer, translation.adminPrivate.chunkClaimed)
        }
        result.onFailure {
            it.printStackTrace()
            messenger.sendMessage(input.claimPlayer, translation.adminPrivate.error)
        }
    }

    private suspend fun unclaim(input: AdminPrivateCommand.Model.UnClaim) {
        val result = runCatching {
            adminPrivateController.unclaim(input.claimPlayer, input.chunk)
        }
        result.onSuccess {
            messenger.sendMessage(input.claimPlayer, translation.adminPrivate.chunkUnClaimed)
        }
        result.onFailure {
            it.printStackTrace()
            messenger.sendMessage(input.claimPlayer, translation.adminPrivate.error)
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
