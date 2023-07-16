package ru.astrainteractive.aspekt.adminprivate.data

import ru.astrainteractive.aspekt.adminprivate.models.AdminChunk
import ru.astrainteractive.aspekt.adminprivate.models.AdminPrivateConfig

interface AdminPrivateRepository {
    fun getConfig(): AdminPrivateConfig
    suspend fun getAllChunks(): List<AdminChunk>
    suspend fun getChunk(chunk: AdminChunk): AdminChunk
    suspend fun saveChunk(chunk: AdminChunk)
    suspend fun deleteChunk(chunk: AdminChunk)
}
