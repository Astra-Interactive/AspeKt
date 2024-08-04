package ru.astrainteractive.aspekt.module.adminprivate.data

import kotlinx.coroutines.withContext
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.adminprivate.data.krate.AdminPrivateKrate
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.util.uniqueWorldKey
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

internal class AdminPrivateRepositoryImpl(
    file: File,
    dispatchers: KotlinDispatchers,
    stringFormat: StringFormat
) : AdminPrivateRepository {

    private val limitedDispatcher = dispatchers.IO.limitedParallelism(1)

    override val krate = AdminPrivateKrate(
        file = file,
        stringFormat = stringFormat
    )

    override suspend fun getAllChunks(): List<AdminChunk> = withContext(limitedDispatcher) {
        val rootConfig = krate.cachedValue
        rootConfig.chunks.map { it.value }
    }

    override suspend fun getChunk(chunk: AdminChunk): AdminChunk = withContext(limitedDispatcher) {
        getAllChunks().firstOrNull {
            it.uniqueWorldKey == chunk.uniqueWorldKey
        } ?: error("Chunk is not under admin claim")
    }

    override suspend fun saveChunk(chunk: AdminChunk) = withContext(limitedDispatcher) {
        val newValue = krate.cachedValue.copy(
            chunks = krate.cachedValue.chunks.toMutableMap().apply {
                this[chunk.uniqueWorldKey] = chunk
            }
        )
        krate.save(newValue)
    }

    override suspend fun deleteChunk(chunk: AdminChunk) = withContext(limitedDispatcher) {
        val newValue = krate.cachedValue.copy(
            chunks = krate.cachedValue.chunks.toMutableMap().apply {
                remove(chunk.uniqueWorldKey)
            }
        )
        krate.save(newValue)
    }
}
