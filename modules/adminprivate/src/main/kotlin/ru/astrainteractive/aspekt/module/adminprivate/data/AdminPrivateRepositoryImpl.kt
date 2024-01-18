package ru.astrainteractive.aspekt.module.adminprivate.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminPrivateConfig
import ru.astrainteractive.aspekt.module.adminprivate.util.uniqueWorldKey
import ru.astrainteractive.astralibs.filemanager.FileManager
import ru.astrainteractive.astralibs.serialization.Serializer
import ru.astrainteractive.astralibs.serialization.SerializerExt.parseOrDefault
import ru.astrainteractive.astralibs.serialization.SerializerExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class AdminPrivateRepositoryImpl(
    private val fileManager: FileManager,
    dispatchers: KotlinDispatchers,
    private val serializer: Serializer = YamlSerializer()
) : AdminPrivateRepository {

    private val limitedDispatcher = dispatchers.IO.limitedParallelism(1)

    override fun getConfig(): AdminPrivateConfig {
        return serializer.parseOrDefault(fileManager.configFile, ::AdminPrivateConfig)
    }

    override suspend fun getAllChunks(): List<AdminChunk> = withContext(limitedDispatcher) {
        val rootConfig = getConfig()
        rootConfig.chunks.map { it.value }
    }

    override suspend fun getChunk(chunk: AdminChunk): AdminChunk = withContext(limitedDispatcher) {
        getAllChunks().firstOrNull {
            it.uniqueWorldKey == chunk.uniqueWorldKey
        } ?: error("Chunk is not under admin claim")
    }

    override suspend fun saveChunk(chunk: AdminChunk) = withContext(limitedDispatcher) {
        val rootConfig = getConfig()
        val newRootConfig = rootConfig.copy(
            chunks = rootConfig.chunks.toMutableMap().apply {
                this[chunk.uniqueWorldKey] = chunk
            }
        )
        serializer.writeIntoFile(newRootConfig, fileManager.configFile)
    }

    override suspend fun deleteChunk(chunk: AdminChunk) = withContext(limitedDispatcher) {
        val rootConfig = getConfig()
        val newRootConfig = rootConfig.copy(
            chunks = rootConfig.chunks.toMutableMap().apply {
                remove(chunk.uniqueWorldKey)
            }
        )
        serializer.writeIntoFile(newRootConfig, fileManager.configFile)
    }
}
