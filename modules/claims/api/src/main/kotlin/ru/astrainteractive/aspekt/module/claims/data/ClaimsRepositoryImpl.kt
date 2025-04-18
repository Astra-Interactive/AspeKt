package ru.astrainteractive.aspekt.module.claims.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotFoundException
import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotOwnedException
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
    private val krateFilesStateFlow = MutableStateFlow(folder.listFiles().orEmpty().toList())
    private val krates = krateFilesStateFlow
        .map { files ->
            files.map { file ->
                val krate = ClaimKrate(
                    file = file,
                    stringFormat = stringFormat,
                    ownerUUID = UUID.fromString(file.nameWithoutExtension)
                )
                krate.loadAndGet()
                krate
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override suspend fun requireKrate(uuid: UUID): ClaimKrate {
        krateFilesStateFlow.update { files ->
            if (files.none { it.nameWithoutExtension == uuid.toString() }) {
                files + folder.resolve("$uuid.yml")
            } else {
                files
            }
        }
        return krates
            .mapNotNull { it.firstOrNull { it.cachedValue.ownerUUID == uuid } }
            .first()
    }

    override fun findKrate(uuid: UUID): ClaimKrate? {
        return krates.value.firstOrNull { it.cachedValue.ownerUUID == uuid }
    }

    override val allKrates: List<ClaimKrate>
        get() = krates.value

    private val _chunkByKrate = krates
        .flatMapLatest { claimKrates ->
            combine(claimKrates.map { it.cachedStateFlow }) {
                buildMap {
                    it.forEachIndexed { i, data ->
                        val krate = claimKrates[i]
                        data.chunks.forEach { chunk ->
                            put(chunk.key, krate)
                        }
                    }
                }
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(scope, SharingStarted.Eagerly, emptyMap())

    override val chunkByKrate: Map<UniqueWorldKey, ClaimKrate>
        get() = _chunkByKrate.value

    override suspend fun saveChunk(uuid: UUID, chunk: ClaimChunk) = mutex.withLock {
        runCatching {
            requireKrate(uuid).update { data ->
                data.copy(
                    chunks = data.chunks.toMutableMap().apply {
                        this[chunk.uniqueWorldKey] = chunk
                    }
                )
            }
        }
    }

    override suspend fun deleteChunk(uuid: UUID, key: UniqueWorldKey) = mutex.withLock {
        runCatching {
            if (key !in chunkByKrate) {
                throw ClaimNotFoundException
            }
            if (!isPlayerOwnClaim(uuid, key)) {
                throw ClaimNotOwnedException
            }
            requireKrate(uuid).update { data ->
                data.copy(
                    chunks = data.chunks
                        .toMutableMap()
                        .minus(key)
                )
            }
        }
    }
}
