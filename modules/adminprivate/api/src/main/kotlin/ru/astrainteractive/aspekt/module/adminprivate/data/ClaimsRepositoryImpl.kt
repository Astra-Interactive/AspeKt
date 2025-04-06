package ru.astrainteractive.aspekt.module.adminprivate.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.adminprivate.data.krate.ClaimKrate
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimData
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.adminprivate.util.uniqueWorldKey
import ru.astrainteractive.klibs.kstorage.suspend.SuspendMutableKrate
import java.io.File
import java.util.UUID

internal class ClaimsRepositoryImpl(
    private val folder: File,
    private val stringFormat: StringFormat
) : ClaimsRepository {
    private val mutex = Mutex()

    private val mutableAllKrates = folder.listFiles().orEmpty().associate { file ->
        UUID.fromString(file.nameWithoutExtension) to ClaimKrate(
            file = file,
            stringFormat = stringFormat
        )
    }.toMutableMap()

    override val allKrates: List<ClaimKrate>
        get() = mutableAllKrates.values.toList()

    override suspend fun getKrate(owner: ClaimPlayer): SuspendMutableKrate<ClaimData> {
        return mutableAllKrates.getOrPut(owner.uuid) {
            ClaimKrate(
                file = folder.resolve("${owner.uuid}.yml"),
                stringFormat = stringFormat
            )
        }
    }

    override suspend fun getChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk): ClaimChunk = mutex.withLock {
        getAllChunks(claimPlayer).firstOrNull {
            it.uniqueWorldKey == chunk.uniqueWorldKey
        } ?: error("Chunk is not under admin claim")
    }

    override suspend fun saveChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk) = mutex.withLock {
        val krate = getKrate(claimPlayer)
        val newValue = krate.cachedValue.copy(
            chunks = krate.cachedValue.chunks.toMutableMap().apply {
                this[chunk.uniqueWorldKey] = chunk
            }
        )
        krate.save(newValue)
    }

    override suspend fun deleteChunk(claimPlayer: ClaimPlayer, chunk: ClaimChunk) = mutex.withLock {
        val krate = getKrate(claimPlayer)
        val newValue = krate.cachedValue.copy(
            chunks = krate.cachedValue.chunks.toMutableMap().apply {
                remove(chunk.uniqueWorldKey)
            }
        )
        krate.save(newValue)
    }
}
