package ru.astrainteractive.aspekt.adminprivate.data

import ru.astrainteractive.aspekt.adminprivate.models.AdminChunk

interface AdminPrivateRepository {
    suspend fun getAllChunks(): List<AdminChunk>
    suspend fun getChunk(chunk: AdminChunk): AdminChunk
    suspend fun saveChunk(chunk: AdminChunk)
    suspend fun deleteChunk(chunk: AdminChunk)
}
