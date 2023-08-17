package ru.astrainteractive.aspekt.adminprivate.data

import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import ru.astrainteractive.aspekt.adminprivate.models.AdminChunk
import ru.astrainteractive.aspekt.adminprivate.models.AdminPrivateConfig
import ru.astrainteractive.aspekt.adminprivate.util.uniqueWorldKey
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.filemanager.FileManager
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class AdminPrivateRepositoryImpl(
    private val fileManager: FileManager,
    dispatchers: KotlinDispatchers
) : AdminPrivateRepository {
    private val limitedDispatcher = dispatchers.IO.limitedParallelism(1)

    override fun getConfig(): AdminPrivateConfig {
        return ConfigLoader().toClassOrDefault(fileManager.configFile, ::AdminPrivateConfig)
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
        fileManager.configFile.writeText(ConfigLoader().defaultYaml.encodeToString(newRootConfig))
    }

    override suspend fun deleteChunk(chunk: AdminChunk) = withContext(limitedDispatcher) {
        val rootConfig = getConfig()
        val newRootConfig = rootConfig.copy(
            chunks = rootConfig.chunks.toMutableMap().apply {
                remove(chunk.uniqueWorldKey)
            }
        )
        fileManager.configFile.writeText(ConfigLoader().defaultYaml.encodeToString(newRootConfig))
    }
}
