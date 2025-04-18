package ru.astrainteractive.aspekt.module.claims.data

import kotlinx.coroutines.sync.withLock
import ru.astrainteractive.aspekt.module.claims.data.getAllChunks
import ru.astrainteractive.aspekt.module.claims.data.krate.ClaimKrate
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimData
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.model.UniqueWorldKey
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.klibs.kstorage.suspend.SuspendMutableKrate

interface ClaimsRepository {
    val allKrates: List<ClaimKrate>
    val chunkByKrate: Map<UniqueWorldKey, ClaimKrate>
    suspend fun getKrate(owner: ClaimPlayer): SuspendMutableKrate<ClaimData>
    suspend fun saveChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk)
    suspend fun deleteChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk)
}

suspend fun ClaimsRepository.getChunk(
    claimPlayer: ClaimPlayer,
    key: UniqueWorldKey
): ClaimChunk? = getAllChunks(claimPlayer).firstOrNull {
    it.uniqueWorldKey == key
}

suspend fun ClaimsRepository.getAllChunks(owner: ClaimPlayer): List<ClaimChunk> {
    return getKrate(owner).loadAndGet().chunks.map { it.value }
}

fun ClaimsRepository.getAllChunks(): List<ClaimChunk> {
    return allKrates
        .map { claimDataKrate -> claimDataKrate.cachedValue }
        .flatMap { claimData -> claimData.chunks.values }
}

fun ClaimsRepository.map(size: Int, chunk: ClaimChunk): Array<Array<Boolean>> {
    val m = Array(size) {
        Array(size) { false }
    }
    val chunks = getAllChunks()
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

fun ClaimsRepository.isAble(
    key: UniqueWorldKey,
    chunkFlag: ChunkFlag,
    claimPlayer: ClaimPlayer? = null
): Boolean {
    val chunkValue = getAllChunks()
        .firstOrNull { it.uniqueWorldKey == key }
        ?.flags
        ?.get(chunkFlag)
        ?: true
    val krate = chunkByKrate[key]
    if (krate == null) {
        return chunkValue
    }
    if (krate.cachedValue.ownerUUID == claimPlayer?.uuid) {
        return true
    }
    if (krate.cachedValue.members.map(ClaimPlayer::uuid).contains(claimPlayer?.uuid)) {
        return true
    }
    return chunkValue
}