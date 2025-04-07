package ru.astrainteractive.aspekt.module.adminprivate.data

import ru.astrainteractive.aspekt.module.adminprivate.data.krate.ClaimKrate
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimData
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimPlayer
import ru.astrainteractive.klibs.kstorage.suspend.SuspendMutableKrate

interface ClaimsRepository {
    val allKrates: List<ClaimKrate>

    suspend fun getKrate(owner: ClaimPlayer): SuspendMutableKrate<ClaimData>
    suspend fun getChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk): ClaimChunk
    suspend fun saveChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk)
    suspend fun deleteChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk)
}

suspend fun ClaimsRepository.getAllChunks(owner: ClaimPlayer): List<ClaimChunk> {
    return getKrate(owner).loadAndGet().chunks.map { it.value }
}

fun ClaimsRepository.getAllChunks(): List<ClaimChunk> {
    return allKrates
        .map { claimDataKrate -> claimDataKrate.cachedValue }
        .flatMap { claimData -> claimData.chunks.values }
}
