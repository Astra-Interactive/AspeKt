package ru.astrainteractive.aspekt.module.claims.controller

import kotlinx.coroutines.Dispatchers
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.data.getAllChunks
import ru.astrainteractive.aspekt.module.claims.data.getChunk
import ru.astrainteractive.aspekt.module.claims.data.isAble
import ru.astrainteractive.aspekt.module.claims.data.map
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

class ClaimController(
    private val repository: ClaimsRepository,
) : CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO),
    Logger by JUtiltLogger("AspeKt-ClaimController") {

    suspend fun map(size: Int, chunk: ClaimChunk): Array<Array<Boolean>> {
        return repository.map(size, chunk)
    }

    suspend fun claim(claimPlayer: ClaimPlayer, claimChunk: ClaimChunk) {
        repository.saveChunk(
            claimPlayer = claimPlayer,
            chunk = claimChunk.copy(
                flags = ChunkFlag.entries.associateWith { false }
            )
        )
    }

    suspend fun unclaim(claimPlayer: ClaimPlayer, claimChunk: ClaimChunk) {
        repository.deleteChunk(claimPlayer, claimChunk)
    }

    suspend fun setFlag(
        claimPlayer: ClaimPlayer,
        flag: ChunkFlag,
        value: Boolean,
        chunk: ClaimChunk
    ) {
        val actualChunk = repository.getChunk(claimPlayer, chunk.uniqueWorldKey) ?: return
        val updatedChunk = actualChunk.copy(
            flags = actualChunk.flags.toMutableMap().apply {
                this[flag] = value
            }
        )
        repository.saveChunk(claimPlayer, updatedChunk)
    }

    fun isAble(
        chunk: ClaimChunk,
        chunkFlag: ChunkFlag,
        claimPlayer: ClaimPlayer? = null
    ): Boolean = repository.isAble(
        chunk.uniqueWorldKey,
        chunkFlag,
        claimPlayer
    )
}
