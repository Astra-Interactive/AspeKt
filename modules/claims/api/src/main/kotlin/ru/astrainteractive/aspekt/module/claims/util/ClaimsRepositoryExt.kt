package ru.astrainteractive.aspekt.module.claims.util

import ru.astrainteractive.aspekt.module.claims.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimNotFoundException
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.model.UnderClaimException
import ru.astrainteractive.aspekt.module.claims.model.UniqueWorldKey
import java.util.UUID

suspend fun ClaimsRepository.claimOwnerUuid(key: UniqueWorldKey): UUID? {
    return chunkByKrate[key]?.cachedStateFlow?.value?.ownerUUID
}

suspend fun ClaimsRepository.isPlayerOwnClaim(uuid: UUID, key: UniqueWorldKey): Boolean {
    return claimOwnerUuid(key) == uuid
}

suspend fun ClaimsRepository.loadAllChunks(uuid: UUID): List<ClaimChunk> {
    return requireKrate(uuid).getValue().chunks.map { entry -> entry.value }
}

suspend fun ClaimsRepository.findChunk(
    uuid: UUID,
    key: UniqueWorldKey
): ClaimChunk? {
    return loadAllChunks(uuid).firstOrNull { claimChunk ->
        claimChunk.uniqueWorldKey == key
    }
}

suspend fun ClaimsRepository.setFlag(
    uuid: UUID,
    flag: ChunkFlag,
    value: Boolean,
    key: UniqueWorldKey
): Result<Unit> {
    val ownerUuid = claimOwnerUuid(key)
    if (ownerUuid != null && ownerUuid != uuid) return Result.failure(UnderClaimException(ownerUuid))
    val actualChunk = findChunk(uuid, key) ?: return Result.failure(ClaimNotFoundException)
    val updatedChunk = actualChunk.copy(
        flags = actualChunk.flags.toMutableMap().apply {
            this[flag] = value
        }
    )
    return saveChunk(uuid, updatedChunk)
}

suspend fun ClaimsRepository.claim(uuid: UUID, claimChunk: ClaimChunk): Result<Unit> {
    claimOwnerUuid(claimChunk.uniqueWorldKey)?.let { ownerUuid ->
        return Result.failure(UnderClaimException(ownerUuid))
    }
    return saveChunk(
        uuid = uuid,
        chunk = claimChunk.copy(
            flags = ChunkFlag.entries.associateWith { false }
        )
    )
}

fun ClaimsRepository.loadAllChunks(): List<ClaimChunk> {
    return allKrates
        .map { claimDataKrate -> claimDataKrate.cachedStateFlow.value }
        .flatMap { claimData -> claimData.chunks.values }
}

fun ClaimsRepository.createGrid(size: Int, chunk: ClaimChunk): Array<Array<Boolean>> {
    val m = Array(size) {
        Array(size) { false }
    }
    val chunks = loadAllChunks()
    for (i in 0 until size) {
        for (j in 0 until size) {
            m[i][j] = chunks.any { claimChunk ->
                val x = chunk.x - size / 2 + i
                val z = chunk.z + size / 2 - j
                claimChunk.x == x && claimChunk.z == z
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
    val chunkValue = loadAllChunks()
        .firstOrNull { claimChunk -> claimChunk.uniqueWorldKey == key }
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
