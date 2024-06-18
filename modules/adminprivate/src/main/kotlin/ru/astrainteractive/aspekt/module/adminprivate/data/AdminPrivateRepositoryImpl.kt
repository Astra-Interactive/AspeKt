package ru.astrainteractive.aspekt.module.adminprivate.data

import kotlinx.coroutines.withContext
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.adminprivate.data.krate.AdminPrivateKrate
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.util.uniqueWorldKey
import ru.astrainteractive.astralibs.filemanager.FileManager
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.klibs.kstorage.util.KrateExt.update
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class AdminPrivateRepositoryImpl(
    fileManager: FileManager,
    dispatchers: KotlinDispatchers,
    stringFormat: StringFormat = YamlStringFormat()
) : AdminPrivateRepository {

    private val limitedDispatcher = dispatchers.IO.limitedParallelism(1)

    override val krate = AdminPrivateKrate(
        file = fileManager.configFile,
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
        krate.update { value ->
            value.copy(
                chunks = value.chunks.toMutableMap().apply {
                    this[chunk.uniqueWorldKey] = chunk
                }
            )
        }
    }

    override suspend fun deleteChunk(chunk: AdminChunk) = withContext(limitedDispatcher) {
        krate.update { value ->
            value.copy(
                chunks = value.chunks.toMutableMap().apply {
                    remove(chunk.uniqueWorldKey)
                }
            )
        }
    }
}
