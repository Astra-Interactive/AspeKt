package ru.astrainteractive.aspekt.module.claims.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.claims.data.krate.ClaimKrate
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimData
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.model.UniqueWorldKey
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.klibs.kstorage.suspend.SuspendMutableKrate
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

    private fun updateChunkByKrate() {
        mutableAllKrates.values.forEach { krate ->
            krate.cachedValue.chunks.forEach { chunk ->
                _chunkByKrate[chunk.key] = krate
            }
        }
    }

    override suspend fun getKrate(owner: ClaimPlayer): SuspendMutableKrate<ClaimData> {
        val krate = mutableAllKrates.getOrPut(owner.uuid) {
            ClaimKrate(
                file = folder.resolve("${owner.uuid}.yml"),
                stringFormat = stringFormat,
                ownerUUID = owner.uuid
            )
        }
        scope.launch { krate.loadAndGet() }
        updateChunkByKrate()
        return krate
    }

    override suspend fun getChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk): ClaimChunk = mutex.withLock {
        getAllChunks(claimPlayer).firstOrNull {
            it.uniqueWorldKey == chunk.uniqueWorldKey
        } ?: error("Chunk is not under admin claim")
    }

    override suspend fun saveChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk) = mutex.withLock {
        getKrate(claimPlayer).update { data ->
            data.copy(
                chunks = data.chunks.toMutableMap().apply {
                    this[chunk.uniqueWorldKey] = chunk
                }
            )
        }
        updateChunkByKrate()
    }

    override suspend fun deleteChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk) = mutex.withLock {
        getKrate(claimPlayer).update { data ->
            data.copy(
                chunks = data.chunks.toMutableMap().apply {
                    remove(chunk.uniqueWorldKey)
                }
            )
        }
        updateChunkByKrate()
    }

    init {
        updateChunkByKrate()
    }
}
