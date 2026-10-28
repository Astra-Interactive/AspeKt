@file:Suppress("FieldDependencyConstruction", "KindImportDirection")

package ru.astrainteractive.aspekt.module.claims.krate

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
import ru.astrainteractive.aspekt.module.claims.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimNotFoundException
import ru.astrainteractive.aspekt.module.claims.model.ClaimNotOwnedException
import ru.astrainteractive.aspekt.module.claims.model.UniqueWorldKey
import ru.astrainteractive.aspekt.module.claims.util.isPlayerOwnClaim
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.klibs.mikro.core.coroutines.propagateCancellationException
import java.io.File
import java.util.UUID

internal class KrateClaimsRepository(
    private val folder: File,
    private val stringFormat: StringFormat,
    private val ioScope: CoroutineScope
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
                krate.getValue()
                krate
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = ioScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override val allKrates: List<ClaimKrate>
        get() = krates.value

    private val _chunkByKrate = krates
        .flatMapLatest { claimKrates ->
            combine(claimKrates.map(ClaimKrate::cachedStateFlow)) { claimDataList ->
                buildMap {
                    claimDataList.forEachIndexed { i, data ->
                        val krate = claimKrates[i]
                        data.chunks.forEach { chunk ->
                            put(chunk.key, krate)
                        }
                    }
                }
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = ioScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    override val chunkByKrate: Map<UniqueWorldKey, ClaimKrate>
        get() = _chunkByKrate.value

    override suspend fun requireKrate(uuid: UUID): ClaimKrate {
        krateFilesStateFlow.update { files ->
            if (files.none { file -> file.nameWithoutExtension == uuid.toString() }) {
                files + folder.resolve("$uuid.yml")
            } else {
                files
            }
        }
        return krates
            .mapNotNull { claimKrates ->
                claimKrates.firstOrNull { krate -> krate.cachedStateFlow.value.ownerUUID == uuid }
            }
            .first()
    }

    override fun findKrate(uuid: UUID): ClaimKrate? {
        return krates.value.firstOrNull { krate -> krate.cachedStateFlow.value.ownerUUID == uuid }
    }

    override suspend fun saveChunk(uuid: UUID, chunk: ClaimChunk): Result<Unit> {
        return mutex.withLock {
            runCatching {
                requireKrate(uuid).save { data ->
                    data.copy(
                        chunks = data.chunks.toMutableMap().apply {
                            this[chunk.uniqueWorldKey] = chunk
                        }
                    )
                }
            }.propagateCancellationException()
        }
    }

    override suspend fun deleteChunk(uuid: UUID, key: UniqueWorldKey): Result<Unit> {
        return mutex.withLock {
            if (key !in chunkByKrate) {
                return@withLock Result.failure(ClaimNotFoundException)
            }
            if (!isPlayerOwnClaim(uuid, key)) {
                return@withLock Result.failure(ClaimNotOwnedException)
            }
            runCatching {
                requireKrate(uuid).save { data ->
                    data.copy(
                        chunks = data.chunks
                            .toMutableMap()
                            .minus(key)
                    )
                }
            }.propagateCancellationException().onFailure { throwable -> throwable.printStackTrace() }
        }
    }
}
