package ru.astrainteractive.aspekt.module.claims.command.claim

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.data.claim
import ru.astrainteractive.aspekt.module.claims.data.map
import ru.astrainteractive.aspekt.module.claims.data.setFlag
import ru.astrainteractive.aspekt.module.claims.messenger.Messenger
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.util.KrateExt.update
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class ClaimCommandExecutor(
    private val messenger: Messenger,
    private val scope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    translationKrate: Krate<PluginTranslation>,
    private val claimsRepository: ClaimsRepository,
    private val claimErrorMapper: ClaimErrorMapper
) : CommandExecutor<Claimommand.Model> {
    val translation by translationKrate

    private suspend fun showMap(claimPlayer: ClaimPlayer, chunk: ClaimChunk) {
        val result = runCatching {
            claimsRepository.map(5, chunk)
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
            val message = claimErrorMapper.toStringDesc(it)
            messenger.sendMessage(claimPlayer, message)
        }
    }

    private suspend fun setFlag(input: Claimommand.Model.SetFlag) {
        val result = claimsRepository.setFlag(
            flag = input.flag,
            value = input.value,
            key = input.chunk.uniqueWorldKey,
            uuid = input.claimPlayer.uuid
        )
        result.onSuccess {
            messenger.sendMessage(input.claimPlayer, translation.claim.chunkFlagChanged)
        }
        result.onFailure {
            val message = claimErrorMapper.toStringDesc(it)
            messenger.sendMessage(input.claimPlayer, message)
        }
    }

    private suspend fun claim(input: Claimommand.Model.Claim) {
        val result = claimsRepository.claim(input.claimPlayer.uuid, input.chunk)
        result.onSuccess {
            messenger.sendMessage(input.claimPlayer, translation.claim.chunkClaimed)
        }
        result.onFailure {
            val message = claimErrorMapper.toStringDesc(it)
            messenger.sendMessage(input.claimPlayer, message)
        }
    }

    private suspend fun unclaim(input: Claimommand.Model.UnClaim) {
        val result = claimsRepository.deleteChunk(input.claimPlayer.uuid, input.chunk.uniqueWorldKey)
        result.onSuccess {
            messenger.sendMessage(input.claimPlayer, translation.claim.chunkUnClaimed)
        }
        result.onFailure {
            val message = claimErrorMapper.toStringDesc(it)
            messenger.sendMessage(input.claimPlayer, message)
        }
    }

    private suspend fun addMember(input: Claimommand.Model.AddMember) {
        val krate = claimsRepository.requireKrate(input.owner.uuid)
        if (input.member in krate.cachedValue.members) {
            messenger.sendMessage(input.owner, translation.claim.alreadyMember)
            return
        }
        krate.update { data ->
            data.copy(members = data.members + input.member)
        }
        messenger.sendMessage(input.owner, translation.claim.memberAdded)
    }

    private suspend fun removeMember(input: Claimommand.Model.RemoveMember) {
        val krate = claimsRepository.requireKrate(input.owner.uuid)
        if (input.member !in krate.cachedValue.members) {
            messenger.sendMessage(input.owner, translation.claim.notMember)
            return
        }
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
