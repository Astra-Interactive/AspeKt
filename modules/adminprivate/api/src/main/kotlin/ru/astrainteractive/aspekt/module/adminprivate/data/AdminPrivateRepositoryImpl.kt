package ru.astrainteractive.aspekt.module.adminprivate.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.adminprivate.data.krate.AdminPrivateKrate
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.util.uniqueWorldKey
import java.io.File

internal class AdminPrivateRepositoryImpl(
    file: File,
    stringFormat: StringFormat
) : AdminPrivateRepository {

    private val mutex = Mutex()

    override val krate = AdminPrivateKrate(
        file = file,
        stringFormat = stringFormat
    )

    override suspend fun getAllChunks(): List<AdminChunk> {
        val rootConfig = krate.cachedValue
        return rootConfig.chunks.map { it.value }
    }

    override suspend fun getChunk(chunk: AdminChunk): AdminChunk = mutex.withLock {
        getAllChunks().firstOrNull {
            it.uniqueWorldKey == chunk.uniqueWorldKey
        } ?: error("Chunk is not under admin claim")
    }

    override suspend fun saveChunk(chunk: AdminChunk) = mutex.withLock {
        val newValue = krate.cachedValue.copy(
            chunks = krate.cachedValue.chunks.toMutableMap().apply {
                this[chunk.uniqueWorldKey] = chunk
            }
        )
        krate.save(newValue)
    }

    override suspend fun deleteChunk(chunk: AdminChunk) = mutex.withLock {
        val newValue = krate.cachedValue.copy(
            chunks = krate.cachedValue.chunks.toMutableMap().apply {
                remove(chunk.uniqueWorldKey)
            }
        )
        krate.save(newValue)
    }
}
