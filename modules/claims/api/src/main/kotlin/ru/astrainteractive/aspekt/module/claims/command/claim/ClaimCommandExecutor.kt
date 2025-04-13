package ru.astrainteractive.aspekt.module.claims.command.claim

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.claims.controller.ClaimController
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.messenger.Messenger
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.util.KrateExt.update
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class ClaimCommandExecutor(
    private val messenger: Messenger,
    private val claimController: ClaimController,
    private val scope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    translationKrate: Krate<PluginTranslation>,
    private val claimsRepository: ClaimsRepository
) : CommandExecutor<Claimommand.Model> {
    val translation by translationKrate

    private suspend fun showMap(claimPlayer: ClaimPlayer, chunk: ClaimChunk) {
        val result = runCatching {
            claimController.map(5, chunk)
        }
        result.onSuccess { claims ->
            messenger.sendMessage(claimPlayer, translation.claim.blockMap)
            claims.forEach { claim ->
                val desc = claim.joinToString("") { if (it) "&#1cba56☒" else "&#c91e1e☒" }
                    .let(StringDesc::Raw)
                messenger.sendMessage(claimPlayer, desc)
            }
        }
        result.onFailure {
            it.printStackTrace()
            messenger.sendMessage(claimPlayer, translation.claim.error)
        }
    }

    private suspend fun setFlag(input: Claimommand.Model.SetFlag) {
        val result = runCatching {
            claimController.setFlag(
                flag = input.flag,
                value = input.value,
                chunk = input.chunk,
                claimPlayer = input.claimPlayer
            )
        }
        result.onSuccess {
            messenger.sendMessage(input.claimPlayer, translation.claim.chunkFlagChanged)
        }
        result.onFailure {
            it.printStackTrace()
            messenger.sendMessage(input.claimPlayer, translation.claim.error)
        }
    }

    private suspend fun claim(input: Claimommand.Model.Claim) {
        val result = runCatching {
            claimController.claim(input.claimPlayer, input.chunk)
        }
        result.onSuccess {
            messenger.sendMessage(input.claimPlayer, translation.claim.chunkClaimed)
        }
        result.onFailure {
            it.printStackTrace()
            messenger.sendMessage(input.claimPlayer, translation.claim.error)
        }
    }

    private suspend fun unclaim(input: Claimommand.Model.UnClaim) {
        val result = runCatching {
            claimController.unclaim(input.claimPlayer, input.chunk)
        }
        result.onSuccess {
            messenger.sendMessage(input.claimPlayer, translation.claim.chunkUnClaimed)
        }
        result.onFailure {
            it.printStackTrace()
            messenger.sendMessage(input.claimPlayer, translation.claim.error)
        }
    }

    private suspend fun addMember(input: Claimommand.Model.AddMember) {
        val krate = claimsRepository.getKrate(input.owner)
        krate.update { data ->
            data.copy(members = data.members + input.member)
        }
        messenger.sendMessage(input.owner, translation.claim.memberAdded)
    }

    private suspend fun removeMember(input: Claimommand.Model.RemoveMember) {
        val krate = claimsRepository.getKrate(input.owner)
        krate.update { data ->
            data.copy(members = data.members - input.member)
        }
        messenger.sendMessage(input.owner, translation.claim.memberRemoved)
    }

    override fun execute(input: Claimommand.Model) {
        when (input) {
            is Claimommand.Model.Claim -> scope.launch(dispatchers.IO) {
                claim(input)
            }

            is Claimommand.Model.SetFlag -> scope.launch(dispatchers.IO) {
                setFlag(input)
            }

            is Claimommand.Model.ShowMap -> scope.launch(dispatchers.IO) {
                showMap(input.claimPlayer, input.chunk)
            }

            is Claimommand.Model.UnClaim -> scope.launch(dispatchers.IO) {
                unclaim(input)
            }

            is Claimommand.Model.AddMember -> scope.launch(dispatchers.IO) {
                addMember(input)
            }

            is Claimommand.Model.RemoveMember -> scope.launch(dispatchers.IO) {
                removeMember(input)
            }
        }
    }
}
