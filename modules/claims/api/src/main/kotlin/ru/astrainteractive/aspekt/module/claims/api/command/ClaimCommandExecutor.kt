package ru.astrainteractive.aspekt.module.claims.api.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.core.plugin.model.PluginTranslation
import ru.astrainteractive.aspekt.module.claims.api.api.ClaimErrorMapper
import ru.astrainteractive.aspekt.module.claims.api.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimCommand
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.api.util.claim
import ru.astrainteractive.aspekt.module.claims.api.util.createGrid
import ru.astrainteractive.aspekt.module.claims.api.util.setFlag
import ru.astrainteractive.aspekt.module.claims.api.util.uniqueWorldKey
import ru.astrainteractive.astralibs.localization.markup.KyoriComponentSerializer
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.propagateCancellationException
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

@Suppress("LongParameterList")
internal class ClaimCommandExecutor(
    private val scope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    private val claimsRepository: ClaimsRepository,
    private val claimErrorMapper: ClaimErrorMapper,
    private val platformServer: PlatformServer,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<PluginTranslation>,
) {
    private val kyori by kyoriKrate
    private val translation by translationKrate

    @Suppress("MagicNumber")
    private fun renderClaimGrid(
        claimPlayer: ClaimPlayer,
        chunk: ClaimChunk
    ) {
        val result = runCatching {
            claimsRepository.createGrid(5, chunk)
        }.propagateCancellationException()
        result.onSuccess { claims ->
            platformServer
                .findOnlinePlayer(claimPlayer.uuid)
                ?.sendMessage(translation.claim.mapTitle)
            claims.forEach { claim ->
                val desc = claim.joinToString("") { isClaimed -> if (isClaimed) "&#1cba56☒" else "&#c91e1e☒" }
                    .let(kyori::toComponent)
                platformServer
                    .findOnlinePlayer(claimPlayer.uuid)
                    ?.sendMessage(desc)
            }
        }
        result.onFailure { error ->
            val message = claimErrorMapper.toMessage(error)
            platformServer
                .findOnlinePlayer(claimPlayer.uuid)
                ?.sendMessage(message)
        }
    }

    private suspend fun setFlag(input: ClaimCommand.SetFlag) {
        val result = claimsRepository.setFlag(
            flag = input.flag,
            value = input.value,
            key = input.chunk.uniqueWorldKey,
            uuid = input.claimPlayer.uuid
        )
        result.onSuccess {
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.sendMessage(translation.claim.flagChanged)
        }
        result.onFailure { error ->
            val message = claimErrorMapper.toMessage(error)
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.sendMessage(message)
        }
    }

    private suspend fun claim(input: ClaimCommand.Claim) {
        val result = claimsRepository.claim(input.claimPlayer.uuid, input.chunk)
        result.onSuccess {
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.sendMessage(translation.claim.claimed)
        }
        result.onFailure { error ->
            val message = claimErrorMapper.toMessage(error)
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.sendMessage(message)
        }
    }

    private suspend fun unclaim(input: ClaimCommand.UnClaim) {
        val result = claimsRepository.deleteChunk(input.claimPlayer.uuid, input.chunk.uniqueWorldKey)
        result.onSuccess {
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.sendMessage(translation.claim.unclaimed)
        }
        result.onFailure { error ->
            val message = claimErrorMapper.toMessage(error)
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.sendMessage(message)
        }
    }

    private suspend fun addMember(input: ClaimCommand.AddMember) {
        val krate = claimsRepository.requireKrate(input.owner.uuid)
        if (input.member in krate.cachedStateFlow.value.members) {
            platformServer
                .findOnlinePlayer(input.owner.uuid)
                ?.sendMessage(translation.claim.alreadyMember)
            return
        }
        krate.save { data ->
            data.copy(members = data.members + input.member)
        }
        platformServer
            .findOnlinePlayer(input.owner.uuid)
            ?.sendMessage(translation.claim.memberAdded)
    }

    private suspend fun removeMember(input: ClaimCommand.RemoveMember) {
        val krate = claimsRepository.requireKrate(input.owner.uuid)
        if (input.member !in krate.cachedStateFlow.value.members) {
            platformServer
                .findOnlinePlayer(input.owner.uuid)
                ?.sendMessage(translation.claim.notMember)
            return
        }
        krate.save { data ->
            data.copy(members = data.members - input.member)
        }
        platformServer
            .findOnlinePlayer(input.owner.uuid)
            ?.sendMessage(translation.claim.memberRemoved)
    }

    fun execute(input: ClaimCommand) {
        when (input) {
            is ClaimCommand.Claim -> scope.launch(dispatchers.IO) {
                claim(input)
            }

            is ClaimCommand.SetFlag -> scope.launch(dispatchers.IO) {
                setFlag(input)
            }

            is ClaimCommand.ShowMap -> scope.launch(dispatchers.IO) {
                renderClaimGrid(input.claimPlayer, input.chunk)
            }

            is ClaimCommand.UnClaim -> scope.launch(dispatchers.IO) {
                unclaim(input)
            }

            is ClaimCommand.AddMember -> scope.launch(dispatchers.IO) {
                addMember(input)
            }

            is ClaimCommand.RemoveMember -> scope.launch(dispatchers.IO) {
                removeMember(input)
            }
        }
    }
}
