package ru.astrainteractive.aspekt.module.claims.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotFoundException
import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotOwnedException
import ru.astrainteractive.aspekt.module.claims.data.exception.UnderClaimException
import ru.astrainteractive.aspekt.module.claims.data.krate.ClaimKrate
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.UniqueWorldKey
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.klibs.kstorage.util.KrateExt.update
import java.io.File
import java.util.UUID

internal class ClaimsRepositoryImpl(
    private val folder: File,
    private val stringFormat: StringFormat,
    private val scope: CoroutineScope
) : ClaimsRepository {
    private val mutex = Mutex()
    private val mutableAllKrates = folder.listFiles().orEmpty().associate { file ->
        val uuid = UUID.fromString(file.nameWithoutExtension)
        val krate = ClaimKrate(
            file = file,
            stringFormat = stringFormat,
            ownerUUID = uuid
        )
        scope.launch { krate.loadAndGet() }
        uuid to krate
    }.toMutableMap()

    override val allKrates: List<ClaimKrate>
        get() = mutableAllKrates.values.toList()

    private val _chunkByKrate = HashMap<UniqueWorldKey, ClaimKrate>()
    override val chunkByKrate: Map<UniqueWorldKey, ClaimKrate>
        get() = _chunkByKrate.toMap()

    private suspend fun updateChunkByKrate() {
        mutableAllKrates.values.forEach { krate ->
            krate.cachedValue.chunks.forEach { chunk ->
                _chunkByKrate[chunk.key] = krate
            }
        }
    }

    override suspend fun getKrate(uuid: UUID): ClaimKrate {
        val krate = mutableAllKrates.getOrPut(uuid) {
            ClaimKrate(
                file = folder.resolve("$uuid.yml"),
                stringFormat = stringFormat,
                ownerUUID = uuid
            )
        }
        scope.launch { krate.loadAndGet() }
        updateChunkByKrate()
        return krate
    }

    override suspend fun saveChunk(uuid: UUID, chunk: ClaimChunk) = mutex.withLock {
        runCatching {
            claimOwnerUuid(chunk.uniqueWorldKey)?.let { claimOwnerUuid ->
                throw UnderClaimException(claimOwnerUuid)
            }
            getKrate(uuid).update { data ->
                data.copy(
                    chunks = data.chunks.toMutableMap().apply {
                        this[chunk.uniqueWorldKey] = chunk
                    }
                )
            }
            updateChunkByKrate()
        }
    }

    override suspend fun deleteChunk(uuid: UUID, key: UniqueWorldKey) = mutex.withLock {
        runCatching {
            if (!isPlayerOwnClaim(uuid, key)) {
                throw ClaimNotOwnedException
            }
            if (key !in chunkByKrate) {
                throw ClaimNotFoundException
            }

            getKrate(uuid).update { data ->
                data.copy(
                    chunks = data.chunks
                        .toMutableMap()
                        .minus(key)
                )
            }
            updateChunkByKrate()
        }
    }

    init {
        scope.launch { updateChunkByKrate() }
    }
}
