package ru.astrainteractive.aspekt.module.claims.controller

import kotlinx.coroutines.Dispatchers
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.data.getAllChunks
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.astralibs.async.CoroutineFeature

class ClaimController(
    private val repository: ClaimsRepository,
) : CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO) {

    suspend fun map(size: Int, chunk: ClaimChunk): Array<Array<Boolean>> {
        val m = Array(size) {
            Array(size) { false }
        }
        val chunks = repository.getAllChunks()
        for (i in 0 until size) {
            for (j in 0 until size) {
                m[i][j] = chunks.any {
                    val x = chunk.x - size / 2 + i
                    val z = chunk.z + size / 2 - j
                    it.x == x && it.z == z
                }
            }
        }
        return m
    }

    suspend fun claim(claimPlayer: ClaimPlayer, claimChunk: ClaimChunk) {
        val actualAdminChunk = claimChunk.copy(
            flags = ChunkFlag.entries.associateWith { false }
        )
        repository.saveChunk(claimPlayer, actualAdminChunk)
    }

    suspend fun unclaim(claimPlayer: ClaimPlayer, claimChunk: ClaimChunk) {
        repository.deleteChunk(claimPlayer, claimChunk)
    }

    suspend fun setFlag(claimPlayer: ClaimPlayer, flag: ChunkFlag, value: Boolean, chunk: ClaimChunk) {
        val actualChunk = repository.getChunk(claimPlayer, chunk)
        val updatedChunk = actualChunk.copy(
            flags = actualChunk.flags.toMutableMap().apply {
                this[flag] = value
            }
        )
        repository.saveChunk(claimPlayer, updatedChunk)
    }

    fun isAble(chunk: ClaimChunk, chunkFlag: ChunkFlag): Boolean {
        val actualChunk = repository
            .getAllChunks()
            .firstOrNull { it.uniqueWorldKey == chunk.uniqueWorldKey }
            ?: return true
        return actualChunk.flags[chunkFlag] ?: false
    }
}
