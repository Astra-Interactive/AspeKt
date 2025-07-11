package ru.astrainteractive.aspekt.module.claims.command.claim

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.data.claim
import ru.astrainteractive.aspekt.module.claims.data.map
import ru.astrainteractive.aspekt.module.claims.data.setFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.MinecraftNativeBridge
import ru.astrainteractive.astralibs.server.PlatformServer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.kstorage.util.update
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

@Suppress("LongParameterList")
class ClaimCommandExecutor(
    private val scope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val claimsRepository: ClaimsRepository,
    private val claimErrorMapper: ClaimErrorMapper,
    minecraftNativeBridge: MinecraftNativeBridge,
    private val platformServer: PlatformServer
) : CommandExecutor<Claimommand.Model>,
    MinecraftNativeBridge by minecraftNativeBridge,
    KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    private suspend fun showMap(
        claimPlayer: ClaimPlayer,
        chunk: ClaimChunk
    ) {
        val result = runCatching {
            claimsRepository.map(5, chunk)
        }
        result.onSuccess { claims ->
            platformServer
                .findOnlinePlayer(claimPlayer.uuid)
                ?.asAudience()
                ?.sendMessage(translation.claim.blockMap.component)
            claims.forEach { claim ->
                val desc = claim.joinToString("") { if (it) "&#1cba56☒" else "&#c91e1e☒" }
                    .let(StringDesc::Raw)
                platformServer
                    .findOnlinePlayer(claimPlayer.uuid)
                    ?.asAudience()
                    ?.sendMessage(desc.component)
            }
        }
        result.onFailure {
            val message = claimErrorMapper.toStringDesc(it)
            platformServer
                .findOnlinePlayer(claimPlayer.uuid)
                ?.asAudience()
                ?.sendMessage(message.component)
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
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.asAudience()
                ?.sendMessage(translation.claim.chunkFlagChanged.component)
        }
        result.onFailure {
            val message = claimErrorMapper.toStringDesc(it)
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.asAudience()
                ?.sendMessage(message.component)
        }
    }

    private suspend fun claim(input: Claimommand.Model.Claim) {
        val result = claimsRepository.claim(input.claimPlayer.uuid, input.chunk)
        result.onSuccess {
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.asAudience()
                ?.sendMessage(translation.claim.chunkClaimed.component)
        }
        result.onFailure {
            val message = claimErrorMapper.toStringDesc(it)
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.asAudience()
                ?.sendMessage(message.component)
        }
    }

    private suspend fun unclaim(input: Claimommand.Model.UnClaim) {
        val result = claimsRepository.deleteChunk(input.claimPlayer.uuid, input.chunk.uniqueWorldKey)
        result.onSuccess {
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.asAudience()
                ?.sendMessage(translation.claim.chunkUnClaimed.component)
        }
        result.onFailure {
            val message = claimErrorMapper.toStringDesc(it)
            platformServer
                .findOnlinePlayer(input.claimPlayer.uuid)
                ?.asAudience()
                ?.sendMessage(message.component)
        }
    }

    private suspend fun addMember(input: Claimommand.Model.AddMember) {
        val krate = claimsRepository.requireKrate(input.owner.uuid)
        if (input.member in krate.cachedStateFlow.value.members) {
            platformServer
                .findOnlinePlayer(input.owner.uuid)
                ?.asAudience()
                ?.sendMessage(translation.claim.alreadyMember.component)
            return
        }
        krate.update { data ->
            data.copy(members = data.members + input.member)
        }
        platformServer
            .findOnlinePlayer(input.owner.uuid)
            ?.asAudience()
            ?.sendMessage(translation.claim.memberAdded.component)
    }

    private suspend fun removeMember(input: Claimommand.Model.RemoveMember) {
        val krate = claimsRepository.requireKrate(input.owner.uuid)
        if (input.member !in krate.cachedStateFlow.value.members) {
            platformServer
                .findOnlinePlayer(input.owner.uuid)
                ?.asAudience()
                ?.sendMessage(translation.claim.notMember.component)
            return
        }
        krate.update { data ->
            data.copy(members = data.members - input.member)
        }
        platformServer
            .findOnlinePlayer(input.owner.uuid)
            ?.asAudience()
            ?.sendMessage(translation.claim.memberRemoved.component)
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
