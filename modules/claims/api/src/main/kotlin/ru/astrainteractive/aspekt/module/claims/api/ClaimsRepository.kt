package ru.astrainteractive.aspekt.module.claims.api

import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimData
import ru.astrainteractive.aspekt.module.claims.model.UniqueWorldKey
import ru.astrainteractive.klibs.kstorage.suspend.StateFlowSuspendMutableKrate
import java.util.UUID

interface ClaimsRepository {
    val allKrates: List<StateFlowSuspendMutableKrate<ClaimData>>
    val chunkByKrate: Map<UniqueWorldKey, StateFlowSuspendMutableKrate<ClaimData>>
    suspend fun requireKrate(uuid: UUID): StateFlowSuspendMutableKrate<ClaimData>
    fun findKrate(uuid: UUID): StateFlowSuspendMutableKrate<ClaimData>?
    suspend fun saveChunk(uuid: UUID, chunk: ClaimChunk): Result<Unit>
    suspend fun deleteChunk(uuid: UUID, key: UniqueWorldKey): Result<Unit>
}
