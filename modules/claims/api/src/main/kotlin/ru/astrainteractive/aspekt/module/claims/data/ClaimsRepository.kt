package ru.astrainteractive.aspekt.module.claims.data

import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotFoundException
import ru.astrainteractive.aspekt.module.claims.data.exception.UnderClaimException
import ru.astrainteractive.aspekt.module.claims.data.krate.ClaimKrate
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.model.UniqueWorldKey
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import java.util.UUID

interface ClaimsRepository {
    val allKrates: List<ClaimKrate>
    val chunkByKrate: Map<UniqueWorldKey, ClaimKrate>
    suspend fun requireKrate(uuid: UUID): ClaimKrate
    fun findKrate(uuid: UUID): ClaimKrate?
    suspend fun saveChunk(uuid: UUID, chunk: ClaimChunk): Result<Unit>
    suspend fun deleteChunk(uuid: UUID, key: UniqueWorldKey): Result<Unit>
}

suspend fun ClaimsRepository.claimOwnerUuid(key: UniqueWorldKey): UUID? {
    return chunkByKrate[key]?.cachedStateFlow?.value?.ownerUUID
}

suspend fun ClaimsRepository.isPlayerOwnClaim(uuid: UUID, key: UniqueWorldKey): Boolean {
    return claimOwnerUuid(key) == uuid
}

suspend fun ClaimsRepository.setFlag(
    uuid: UUID,
    flag: ChunkFlag,
    value: Boolean,
    key: UniqueWorldKey
): Result<Unit> {
    claimOwnerUuid(key)?.let { ownerUuid ->
        if (ownerUuid != uuid) throw UnderClaimException(ownerUuid)
    }
    val actualChunk = getChunk(uuid, key) ?: return Result.failure(ClaimNotFoundException)
    val updatedChunk = actualChunk.copy(
        flags = actualChunk.flags.toMutableMap().apply {
            this[flag] = value
        }
    )
    return saveChunk(uuid, updatedChunk)
}

suspend fun ClaimsRepository.getChunk(
    uuid: UUID,
    key: UniqueWorldKey
): ClaimChunk? = getAllChunks(uuid).firstOrNull {
    it.uniqueWorldKey == key
}

suspend fun ClaimsRepository.getAllChunks(uuid: UUID): List<ClaimChunk> {
    return requireKrate(uuid).getValue().chunks.map { it.value }
}

suspend fun ClaimsRepository.claim(uuid: UUID, claimChunk: ClaimChunk): Result<Unit> {
    claimOwnerUuid(claimChunk.uniqueWorldKey)?.let { ownerUuid ->
        throw UnderClaimException(ownerUuid)
    }
    return saveChunk(
        uuid = uuid,
        chunk = claimChunk.copy(
            flags = ChunkFlag.entries.associateWith { false }
        )
    )
}

fun ClaimsRepository.getAllChunks(): List<ClaimChunk> {
    return allKrates
        .map { claimDataKrate -> claimDataKrate.cachedStateFlow.value }
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
    if (krate.cachedStateFlow.value.ownerUUID == claimPlayer?.uuid) {
        return true
    }
    if (krate.cachedStateFlow.value.members.map(ClaimPlayer::uuid).contains(claimPlayer?.uuid)) {
        return true
    }
    return chunkValue
}
