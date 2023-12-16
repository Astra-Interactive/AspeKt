package ru.astrainteractive.aspekt.module.adminprivate.data

import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminPrivateConfig

interface AdminPrivateRepository {
    fun getConfig(): AdminPrivateConfig
    suspend fun getAllChunks(): List<AdminChunk>
    suspend fun getChunk(chunk: AdminChunk): AdminChunk
    suspend fun saveChunk(chunk: AdminChunk)
    suspend fun deleteChunk(chunk: AdminChunk)
}
