package ru.astrainteractive.aspekt.adminprivate.data

import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import ru.astrainteractive.aspekt.adminprivate.models.AdminChunk
import ru.astrainteractive.aspekt.adminprivate.models.AdminPrivateConfig
import ru.astrainteractive.aspekt.adminprivate.util.uniqueWorldKey
import ru.astrainteractive.astralibs.async.KotlinDispatchers
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.filemanager.FileManager

class AdminPrivateRepositoryImpl(
    private val fileManager: FileManager,
    private val dispatchers: KotlinDispatchers
) : AdminPrivateRepository {
    private val limitedDispatcher = dispatchers.IO.limitedParallelism(1)

    override suspend fun getAllChunks(): List<AdminChunk> = withContext(limitedDispatcher) {
        val rootConfig = ConfigLoader.toClassOrDefault(fileManager.configFile, ::AdminPrivateConfig)
        rootConfig.chunks.map { it.value }
    }

    override suspend fun getChunk(chunk: AdminChunk): AdminChunk = withContext(limitedDispatcher) {
        getAllChunks().firstOrNull {
            it.uniqueWorldKey == chunk.uniqueWorldKey
        } ?: error("Chunk is not under admin claim")
    }

    override suspend fun saveChunk(chunk: AdminChunk) = withContext(limitedDispatcher) {
        val rootConfig = ConfigLoader.toClassOrDefault(fileManager.configFile, ::AdminPrivateConfig)
        val newRootConfig = rootConfig.copy(
            chunks = rootConfig.chunks.toMutableMap().apply {
                this[chunk.uniqueWorldKey] = chunk
            }
        )
        fileManager.configFile.writeText(ConfigLoader.defaultYaml.encodeToString(newRootConfig))
    }

    override suspend fun deleteChunk(chunk: AdminChunk) = withContext(limitedDispatcher) {
        val rootConfig = ConfigLoader.toClassOrDefault(fileManager.configFile, ::AdminPrivateConfig)
        val newRootConfig = rootConfig.copy(
            chunks = rootConfig.chunks.toMutableMap().apply {
                remove(chunk.uniqueWorldKey)
            }
        )
        fileManager.configFile.writeText(ConfigLoader.defaultYaml.encodeToString(newRootConfig))
    }
}
